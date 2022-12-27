async function openEmail (file) {

		document.querySelector("#msg").innerHTML = 
		`<div class="spinner-border" role="status">
		  <span class="sr-only">Loading...</span>
		</div>`;
			const response = await fetch ('/postbox/service/postboxEmail?filename='+file);
			const json = await response.json();
			
			let resp = `<p class="text-center"><h2>${json["subject"]}</h2></p>`;
			let text;
			let html;
			let dwnld = '<div class="row">';
		
			json["content"].forEach((item) => {
				
				if (item["content-type"].match("text/plain")) {
					//text = `<p class="text-center">${item["content"]}</p>`; 
				}
				else if (item["content-type"].match("text/html")) {
					//html = `<p class="text-center">${item["content"]}</p>`;
				}
				else {
					var filenameRegex = /name[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
					var splitted = item["content-type"].split(";");
					var matches = filenameRegex.exec(item["content-type"]);
            		if (matches != null && matches[1]) {
            			filename = matches[1].replace(/['"]/g, '');
          			
            			dwnld += `<div class="col-3">
            			<a download="${filename}" type="button" class="btn btn-dark btn-lg" href="data:${splitted[0].trim()};base64,${item["content"]}"><i class="bi bi-download"></i> Download</a>
            			</div>`

					}
				}

			 });
			 
			 dwnld += '</div>';
			
			if (html!=null) {
				resp += html + dwnld;
			}
			else if (text!=null) {
				resp += text + dwnld;
			}
			else {
				resp += dwnld;
			}
			
		     
	        document.querySelector("#msg").innerHTML = resp;
		  
};

