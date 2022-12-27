import { html, render } from "../lib/lit-html-module.js";
import { replaceOrCreateTag } from "../lib/utils.js";
import { PostboxModal } from "./postbox-modal.js"
import { PostboxModalTestData } from "./postbox-modal-testdata.js"

class PostboxClient extends HTMLElement {
  
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
    this.getEntities();
    
  }
  
  async addTestData () {
  //PostboxModal;
  	 const dialog = replaceOrCreateTag("postbox-modal-testdata");
     await document.body.appendChild(dialog);
  };    
  
  async openEmail (file) {
  //PostboxModal;
  	 const dialog = replaceOrCreateTag("postbox-modal");
     await document.body.appendChild(dialog);
     dialog.load('/commbox/service/postboxEmail?filename='+file); 		
		  
  };  
  
  async getEntities() {
		const response = await fetch (`/commbox/service/postboxEntities`);
		let files; 
		
		try {
			
			if (response.status != 200) {
				window.location = './'
			}
			else {
				files = await response.json();
				const options = { day: "2-digit", month: "2-digit", year: "numeric" };
			
				if (files.length == 0) {
					document.querySelector("#result").innerHTML = `Nix da`;
				}
				else {
				var arr = [];
				arr.push(html `<div class="row">`);
				files.forEach((item) => {
					
					const date = new Date(item["insertation_dt"]);
				    const d = date.toLocaleDateString("de-DE", options);
			    	
					arr.push(html `
					<div class="col-12">
					<a href='#' id='${item["filename"]}'
					  @click=${(event) => {
	                 	event.preventDefault();
	                 	this.openEmail(item["filename"]);
	               			}} 
	               			 class="list-group-item list-group-item-action flex-column align-items-start">
					    <div class="d-flex w-100 justify-content-between">
					      <h5 class="mb-1">${item["subject"]}</h5>
					      <small>${d}</small>
					    </div>
					    <p class="mb-1">${item["preview"]}</p>
					    
					  </a>
					  </div>
					  `);
				
			    });
			      
			     arr.push(html `</div>
			      <div class="col-8">
					   <div id="msg"></div>
					  </div>
				   </div>`);
				   render(arr,document.querySelector("#result"));
				}
			}
		
		}
		catch (e){
			console.log(e);
		    document.querySelector("#result").innerHTML = 'Fehler';
		}
  

	
	}
	
  view() {
     return html`
     
     <div class="header row g-1 mt-1" id="header">
        <div class="col-md-10">
          <h1>Deine Postbox</h1>
        </div>  
        <div class="col-md-2">
           <button id="addTestData" type="button" class="btn btn-lg mb-3 w-100 text-uppercase" style="background-color:#E95D0F;" @click=${(e) => {e.preventDefault(); this.addTestData(); } }> <span id="execute-spinner" role="status" class="spinner-border-sm"></span>Add Testdata</button>
        </div>  
     </div>
     <br>
     <div id="result">
	     <div class="row g-1 mt-1">
	        <div class="col-md-5">
	        </div>
	        
	        <div class="col-md-2">
	            <div class="spinner-border" role="status">
				  <span class="sr-only">Loading...</span>
				</div>
	        </div>
	        <div class="col-md-5">
	        </div>
	        
	     </div>
     </div>
 
   `;
  }

  async show(){

	spinner.classList.remove("spinner-border");
    }
}


customElements.define("postbox-client", PostboxClient);