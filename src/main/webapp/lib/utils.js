const convertToSlug = (str) => {
  return str
  .toLowerCase()
  .replace(/[^\w ]+/g, '')
  .replace(/ +/g, '-');
};

const handleNavigationToggleEvent = (event, item) => {
  event.preventDefault();
  if (!!item) {
    closeAllDropdowns();
  } else {
    const ulSelector = event.target.parentNode.querySelector('ul');
    const currentLevel = ulSelector.getAttribute('level');
    if (ulSelector.getElementsByTagName("li").length==0) {
      closeAllDropdowns();
      return;
    }
    
    if (!ulSelector.classList.contains('show')) {
      document.querySelectorAll(`ul[level="${currentLevel}"]`).forEach(element => element.classList.remove('show'));
      ulSelector.classList.add('show');
    } else {
      ulSelector.classList.remove('show');
    }
  }
}

const closeAllDropdowns = () => {
  document
    .querySelectorAll('nav .dropdown .show, .navbar-collapse:not(.collapse)')
    .forEach((element) => {
      if (element.classList.contains('show')) {
        element.classList.remove('show');
      } else {
        element.classList.add('collapse');
      }
    });
}

const graphColors = () => {
  return [
    "#088A08",
    "#2E64FE",
    "#e67e22",
    "#218080",
    "#8e44ad",
    "#2ecc71",
    "#2980b9",
    "#f1c40f",
    "#748735",
    "#00FFFF","#0000FF","#00008B","#ADD8E6","#800080","#FFFF00","#00FF00","#FF00FF","#FFC0CB",
    "#C0C0C0","#808080","#FFA500","#A52A2A","#800000","#008000","#808000","#7FFD4"
  ];
}

const replaceOrCreateTag = (tag) => {
  for (const element of document.getElementsByTagName(tag)) {
     element.parentNode.removeChild(element);
  }
  return document.createElement(tag);
}

export { convertToSlug, handleNavigationToggleEvent, closeAllDropdowns, graphColors, replaceOrCreateTag };
