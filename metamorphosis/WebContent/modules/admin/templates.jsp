<%@ taglib prefix="s" uri="/struts-tags"%>
<h1 class="template-32">Templates
<a title="Upload" class="upload-16">Upload</a>
</h1>
<div id="content">
<fieldset class="extensions-details">
	<div id="extensions">
	
	   <s:iterator value="#application.templateManager.templates">
	   
	      <div class="extension">
	      <img class="selected" src="${images}/selected-32.png" style="display:${template==name?'inline-block':'none'}"/>
		  <a>
		  <img src="${thumbnail}"/>
		  </a>
		  <div class="info">
		  <span class="extension-name">${name}</span>
		  <span>${author}</span>
		  <span>Type : ${type}</span>
		  </div>
		  <div class="extensionDetails">
		      	  <span>Name : ${name}</span>
		      	  <span>Type : ${type}</span>
				  <span>Author : ${author}</span>
				  <span>Author Email : ${authorEmail}</span>
				  <span>Author Url : ${authorUrl}</span>
				  <span>Creation Date : ${creationDate}</span>
				  <span>Description : ${description}</span>
				  <span>Copyright : ${copyright}</span>
				  <span>License : ${license}</span>
				  <span>Version : ${version}</span>
		 </div>
		</div>
		
	   </s:iterator>
			
	</div>
</fieldset>
</div>
<script type="text/javascript" src="${js}/extensions.js"></script>