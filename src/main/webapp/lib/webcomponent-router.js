class WebcomponentRouter {
  constructor(renderTarget, routeArray) {
    this.renderTarget = renderTarget;

    // check if the given parameter is html element
    try {
      this.renderTarget.constructor.__proto__.prototype.constructor.name;
    } catch(_e) {
      return console.error('the given render target is no HTMLElement or does not exists');
    }
    this._setRoutes(routeArray);

    this.baseHost = window.location.host;
    this.currentRoute = null;
    this.currentPath = null;
    this.search = null;

    document.addEventListener('click', (event) => {
      let updateUrl = true;
      const tag = event.target.cloneNode(false);
      // Only anchor's with the data-attribute: `routing` will be recognized and processed by the router.
      // That is the easiest way to delegate the click of nested anchors to the anchor itself to be safe that a
      // href comparision is possible.
      if (!tag.hasAttribute('data-routing')) return;
      if (!tag.nodeName || tag.nodeName.toLowerCase() != 'a') return;
      if (tag.host != this.baseHost) return;
      if (tag.getAttribute('href') === '#') return;

      let newPath;
      if (tag.getAttribute('href').startsWith('/#/')) {
        tag.pathname = this.basePath;
        newPath = `${this.basePath}/${tag.hash}`;
      } else {
        newPath = tag.href;
      }

      if (this.currentPath == newPath) {
        updateUrl = false;
      }
      event.preventDefault();

      if (updateUrl) {
        window.history.pushState('Irgendwas', 'Title', newPath);
        this.searchAndRender(tag);
      }
    });

    addEventListener('popstate', _ => this.searchAndRender(this.historyChangeLocationObj()));
    addEventListener('hashchange', _ => this.searchAndRender(this.historyChangeLocationObj()));
  }

  _setRoutes(routeArray) {
    let clonedLocation = Object.assign({}, window.location);
    this.basePath = this.setupBasePath(clonedLocation.pathname);
    clonedLocation.pathname = this.basePath;
    this.configuredRoutes = routeArray.map((route) => {
      if (route.path.startsWith('#')) {
        route['path'] = `/${route.path}`;
      }

      if (route.path == '/' || route.path.startsWith('/#/')) {
        route['path'] = `${this.basePath}${route.path}`;
      }

      return route;
    });

    this.searchAndRender(clonedLocation);
  }

  historyChangeLocationObj() {
    let clonedLocation = Object.assign({}, window.location);
    if (clonedLocation.pathname === '/')
        clonedLocation.pathname = this.basePath;

    return clonedLocation;
  }

  view() {
    const component = this.currentRoute.component;
    const options = {};
    if (this.currentRoute.hasOwnProperty('is')) options.is = this.currentRoute.is;
    const renderElement = document.createElement(component, options);
    renderElement.router = {}
    renderElement.currentPath=this.currentPath;

    if (Object.entries(this.currentRoute.componentParams).length > 0) {
        renderElement.router = this.currentRoute.componentParams;
    }
    
    if (this.search!=null) {
        for (const [key,value] of this.search) {
          renderElement.router[key]=value;
        }
    }
    
    return renderElement;
  }

  relativePathWithHash(obj) {
    let value = '';
    value += obj.pathname;
    if (!obj.pathname.endsWith('/')) value += '/';
    value += obj.hash;

    const qm = value.indexOf('?');
    if (qm>0) {
      const query = value.substring(qm+1);
      value = value.substring(0,qm);  
      this.search = new URLSearchParams(query);
    }
    else {
      this.search=null;
    }

    return value;
  }

  searchAndRender(obj) {
    this.currentPath = this.relativePathWithHash(obj);
    this.currentRoute = undefined;

    // TODO: Check the result of the compareRoutesAndPath fn
    // add result.route to this.currentRoute
    // Pass component params to view() fn to add custom attributes to web components that should get rendered
    // attention: currently the custom attributes starts with a :, this should be removed
    const matchingResultObject = this.compareRoutesAndPath();
    this.currentRoute = matchingResultObject.route;

    if (this.currentRoute === undefined) {
      console.error('No route found.');
      return;
    }
    this.currentRoute['componentParams'] = matchingResultObject.componentParams;

    if (this.currentRoute.hasOwnProperty('action')) {
      this.currentRoute.action();
    }
    this.renderTarget.replaceChildren(this.view());
  }

  compareRoutesAndPath() {
    let obj = { route: undefined, componentParams: {} };

    // TODO: this is pretty inefficient because we loop through this every request
    // Try to cache things
    for (let i = 0; i < this.configuredRoutes.length; i++) {
      const route = this.configuredRoutes[i];
      if (route.path.includes(':')) {
        const routeMatcher = route.path.match(this.urlRegex());
        const currentUrlMatcher = this.currentPath.match(this.urlRegex());

        const namedRouteIndices = [];
        let compareUrl = Array.from(routeMatcher);

        for (let i = 0; i < routeMatcher.length; i++) {
          if (routeMatcher[i].startsWith(':')) {
            namedRouteIndices.push(i);
          }
        }

        namedRouteIndices.forEach(idx => compareUrl[idx] = currentUrlMatcher[idx]);

        if (compareUrl.join('/') === currentUrlMatcher.join('/')) {
          obj.route = route;
          namedRouteIndices.forEach(idx => {
            // remove the : character so that the parameter can be easier accesed in the target component
            obj.componentParams[routeMatcher[idx].substring(1)] = currentUrlMatcher[idx];
          });

          break;
        }
      } else {
        if (route.path === this.currentPath) {
          obj.route = route;
          break;
        }
      }
    };

    return obj;
  }

  urlRegex() {
    const regex = new RegExp('([^\/]+)', 'g');

    return regex;
  }

  setupBasePath(pathname) {
    const pathWithoutFileEnding = pathname.substring(0, pathname.lastIndexOf('/'));

    if (pathWithoutFileEnding.endsWith('/')) return pathWithoutFileEnding.slice(0, -1);

    return pathWithoutFileEnding;
  }
}

export { WebcomponentRouter };
