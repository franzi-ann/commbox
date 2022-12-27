const urlPaths = () => {
    let prefix = `${window.location.protocol}//${window.location.hostname}/`;
    let path = window.location.pathname.split('/').filter(a => a !="" && a.toLowerCase().includes('csmp'))[0];
    let jsPrefix = '';
    if (path == undefined) {
      path = 'csmp';
      jsPrefix = '/';
    }

    return {
        digest: `${prefix}${path}/service/digest`,
        fetch: `${prefix}${path}/service/fetch`,
        config: `${prefix}${path}/service/config`,
        debug: `${prefix}${path}/service/debug`,
        jsconsole: `${prefix}${path}/service/debug/js`,
        alarm: `${prefix}${path}/service/alertlist`,
        calendar: `${prefix}${path}/service/calendar`,
        details: `${prefix}${path}/service/details`,
        hasrefresh: `${prefix}${path}/service/hasrefresh`,
        alertDetail: `${prefix}${path}/service/alert`,
        icingaAlerts: `${prefix}${path}/service/icinga-dashboard`,
        icingaAlertDetail: `${prefix}${path}/service/icinga-detail`,
        // javascript routes`
        alertJsDetail: `${jsPrefix}#/alerts`,
        graphJsDetail: `${jsPrefix}#/graphs`,
        detailJsDetail: `${jsPrefix}#/details`,
        icingaAlertJsDetail: `${jsPrefix}#/icinga`,
        receive: `${prefix}${path}/receive`
    }
}

export { urlPaths };
