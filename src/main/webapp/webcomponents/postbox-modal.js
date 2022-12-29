import { html, render } from "../lib/lit-html-module.js";
import { Modal } from "../lib/bootstrap.esm.js"

/*Diese Klasse dient der Darstellung einzelner Emails*/
export class PostboxModal extends HTMLElement {
  
  constructor() {
    super();
  }
  
  onresize ()
   {
    
   }


  disconnectedCallback() {
  }

  connectedCallback() {
     this.classList.add ("modal");
     this.classList.add ("fade")
     this.setAttribute("role","dialog");
     this.setAttribute("aria-hidden","false");
     this.setAttribute("tabindex","-1");
     //this.setAttribute("data-bs-target","#staticBackdrop");
     this.setAttribute("data-bs-backdrop","static");
     this.setAttribute("data-bs-keyboard","false");
     
     render (this.view(), this);
     this.modal = Modal.getOrCreateInstance(this);
     this.modal.s
     this.modal.show();
  }
  
  close ()
  {
     this.modal.dispose();
     this.parentElement.removeChild(this);
  }
  
  
  view() {
     return html`
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <div id="emailModalHeader"><h1 class="modal-title fs-5" id="staticBackdropLabel">Loading</h1></div>
		      </div>
		      <div class="modal-body">
		        <div id="emailModalBody"><span class="sr-only">Loading...</span></div>
		        <br>
		        <div id="emailModalDwnld" style="background-color:#00998a;"></div>
		        <div id="loading-error"></div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn-close" aria-label="Close" @click=${(e) => this.close()}>
		      </div>
		    </div>
		  </div>`;
  }
  
   async load (urlPath)
  {
     try {
        const request = await fetch (urlPath);
		const data = await request.json();
        let textContent;
        let htmlContent;
        let dwnldContent='';
        if (request.status == 200) {
           document.getElementById("emailModalHeader").innerHTML=data.subject;
           
           	data["content"].forEach((item) => {
				if (item["content-type"].match("text/plain")) {
					textContent = `${item["content"]}`; 
				}
				else if (item["content-type"].match("text/html")) {
					htmlContent = `${item["content"]}`;
				}
				else {
					var filenameRegex = /name[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
					var splitted = item["content-type"].split(";");
					var matches = filenameRegex.exec(item["content-type"]);
            		if (matches != null && matches[1]) {
            			var filename = matches[1].replace(/['"]/g, '');
            			dwnldContent += `<div class="col-3">
            			<a download="${filename}" type="button" class="btn btn-dark btn-lg" href="data:${splitted[0].trim()};base64,${item["content"]}"><i class="bi bi-download"></i> Download</a>
            			</div>`
					}
				}
			 });
           
           
           if (textContent!=null || htmlContent!=null ) document.getElementById("emailModalBody").innerHTML=htmlContent!=null?htmlContent:textContent;
           document.getElementById("emailModalDwnld").innerHTML=dwnldContent;
           
        }
        else {
          this.show_error (`Laden fehlgeschlagen: (${request.status}): ${request.statusText}`);
        }
      } catch(_e) {
         this.show_error (`Laden fehlgeschlagen: ${_e}`);
      }
  }
  
  close ()
  {
     this.modal.dispose();
     this.parentElement.removeChild(this);
  }
    
  show_error (error)
  {
     document.getElementById("emailModalHeader").innerHTML="Fehler";
     document.getElementById("emailModalBody").innerHTML="";
     const div = document.getElementById("loading-error");
     div.innerText = error;
     div.classList.remove("d-none");
  }
	
}


customElements.define("postbox-modal", PostboxModal);
