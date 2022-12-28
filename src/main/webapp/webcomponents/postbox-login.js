import { html, render } from "../lib/lit-html-module.js";

/*Diese Klasse ist die GUI für die Login-Page.*/
class PostboxLogin extends HTMLElement {
  
  constructor() {
    super();
  }
  
  onresize ()
   {
    
   }


  disconnectedCallback() {
  }

  async connectedCallback() {
    render (this.view(),this);
   
  }
  
  async doLogin(e) {
	e.preventDefault();
	document.getElementById("loginButton").disabled = true;
    var spinner = (html ` 
    	<div class="row g-1 mt-1">
        	<div class="col-md-3"></div>
        		<div class="col-md-6">
	    			<div class="spinner-border" role="status">
					  <span class="sr-only">Loading...</span>
					</div>
    			</div>
    		</div>	
        </div>`);
    render(spinner, document.querySelector("#ausgabe"));

	const user = document.getElementById("userField").value;
	const password = document.getElementById("passwordField").value;
	
	const response = await fetch (`/commbox/login?email=${user}&password=${password}`,{
		method:'POST'
	});

      
    if (response.status==200) {
		const json = await response.json();
		
		console.log(json);	//Wenn der Status 200=ok , dann soll der wobi angezeigt werden
     	window.location = './#/client'
      
    }
  	else {
		render(html`Das hat nicht funktioniert....`, document.querySelector("#ausgabe"));
		document.getElementById("loginButton").disabled = false; 	
	}
  }
  
    
  view() {
     return html`
		<div class="sidenav">
	         <div class="login-main-text">
	            <h2>Commbox<br> Login Page</h2>
	         </div>
	      </div>
	      <div class="main">
	         <div class="col-md-6 col-sm-12">
	            <div class="login-form">
	               <form>
	                  <div class="form-group">
	                     <label>User Name</label>
	                     <input type="text" class="form-control" id="userField" placeholder="User Name">
	                  </div>
	                  <div class="form-group">
	                     <label>Password</label>
	                     <input type="password" class="form-control" id="passwordField" placeholder="Password">
	                  </div>
	                  <br>
	                  <button type="submit" class="btn btn-black" id="loginButton" @click=${(e) => { this.doLogin(e); } }>Login</button>
	               </form>
	            </div>
	            <div id="ausgabe"></div>
	         </div>
	      </div>
   `;
  }

  async show(){

	spinner.classList.remove("spinner-border");
    }
}


customElements.define("postbox-login", PostboxLogin);