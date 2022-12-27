import "../webcomponents/postbox-client.js";
import "../webcomponents/postbox-login.js";
import { WebcomponentRouter } from "../lib/webcomponent-router.js";
import "../lib/popper.js";
import "../lib/bootstrap.esm.js";

const routerTarget = document.querySelector('#router-target');
const routes = [
  {
    path: '/',
    component: 'postbox-login',
    action: async () => { await import("../webcomponents/postbox-login.js") }
  },
  {
    path: '/#/client',
    component: 'postbox-client',
    action: async () => { await import("../webcomponents/postbox-client.js") }
  }
];

new WebcomponentRouter(routerTarget, routes);

window.onresize = () => {
  const evt = new CustomEvent("resizeWindow", { bubbles: true });
  dispatchEvent(evt);
};

