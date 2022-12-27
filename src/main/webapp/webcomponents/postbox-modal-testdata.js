import { html, render } from "../lib/lit-html-module.js";
import { Modal } from "../lib/bootstrap.esm.js"


export class PostboxModalTestData extends HTMLElement {
  
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
		        <div id="emailModalHeader"><h1 class="modal-title fs-5" id="staticBackdropLabel">Test Email generieren</h1></div>
		      </div>
		      <div class="modal-body">
		        <div id="emailModalBody">
		        
				  <form>
				    <div class="button-group">
				      <input type="radio" name="button-group" id="send_email" value="Nur Email senden" checked @click=${(event) => { event.target.checked = true }}>
				      <label>Nur Email senden</label>
				      <input type="radio" name="button-group" id="archive_email" value="Archivieren" @click=${(event) => { event.target.checked = true }}>
				      <label>Archivieren</label>
				      <input type="radio" name="button-group"  id="notify_email"  value="Archivieren & Notifizieren" @click=${(event) => { event.target.checked = true }}>
				      <label>Archivieren & Notifizieren</label>
				    </div>
				  </form>		        
		        </div>
		        <br>
		        <div id="emailModalBtn">
		        	<button id="addTestData" type="button" class="btn btn-lg mb-3 w-100 text-uppercase" style="background-color:#E95D0F;" @click=${() => { this.sendEmail() } }>Email senden</button
		        </div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn-close" aria-label="Close" @click=${(e) => this.close()}>
		      </div>
		    </div>
		  </div>`;
  }
  
   async sendEmail ()
  {
     try {
		var testType = 0;
		if (document.querySelector("#archive_email").checked)
			testType = 1
		else if (document.querySelector("#notify_email").checked)
			testType = 2;
			
        const request = await fetch ("/commbox/service/addTestEmail?type="+testType);

        if (request.status == 200) {
		   render(html`Email versendet`,document.querySelector("#emailModalHeader"));
		   render(html`Du kannst diese Seite jetzt schließen und die Haupseite neuladen`,document.querySelector("#emailModalBody"));
		   render(html``,document.querySelector("#emailModalBtn"));
           
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
	console.log(error);
	render(html`Fehler`,document.querySelector("#emailModalHeader"));
	render(html`Das hat nicht funktioniert`,document.querySelector("#emailModalBody"));
    render(html``,document.querySelector("#emailModalBtn"));
  }
	
}


customElements.define("postbox-modal-testdata", PostboxModalTestData);
