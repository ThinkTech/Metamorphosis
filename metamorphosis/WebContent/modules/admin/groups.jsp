<%@ taglib prefix="s" uri="/struts-tags"%>
<h1>List Of Groups <a id="create" href="#form">Create</a></h1>
<form id="search">
	<span>Filter :</span>
	<select name="filter">
	   <option>Name</option>
	</select>
	<input name="value" type="text"  placeholder="Search..."/>
</form>

<div id="list">
<table>
 <thead>
    <tr>
       <th title="Name">Name</th>
       <th title="Created By">Created By</th>
    </tr>
 </thead>

<tbody>
   <template type="text/x-dust-template">
     {#.}
      <tr id="{id}">
   	        <td>{name}</td>
   	    	<td>{createdBy}</td>
   	    </tr>
     {/.}
   </template>
   
 </tbody>
  
</table>

</div>

<div id="selection"><span>select a group</span></div>

<div id="details">

<div id="tabs"> 
   <div id="informations" title="Informations">
     <template  type="text/x-dust-template">

       <fieldset>
		<span>Name : </span>
		<span>{name}</span>
		
	  </fieldset>

     </template>
     
   </div>
      <div title="Permissions">
    <template  type="text/x-dust-template">
      <s:iterator value="#application.moduleManager.adminModules">
       <fieldset>
       <span>Module ${name}</span>
       <span></span>
        <span>Create : </span>
        <span>yes<input type="radio" name="${id}.create" value="yes" checked/>
        no<input type="radio" name="${id}.create" value="no"/>
       </span>
        <span>Read : </span>
        <span>yes<input type="radio" name="${id}.read" value="yes" checked/>
        no<input type="radio" name="${id}.read" value="no"/>
       </span>
       <span>Update : </span>
        <span>yes<input type="radio" name="${id}.update" value="yes" checked/>
        no<input type="radio" name="${id}.update" value="no"/>
       </span>
       <span>Delete : </span>
        <span>yes<input type="radio" name="${id}.delete" value="yes" checked/>
        no<input type="radio" name="${id}.delete" value="no"/>
       </span>
       <span>PDF : </span>
        <span>yes<input type="radio" name="${id}.pdf" value="yes" checked/>
        no<input type="radio" name="${id}.pdf" value="no"/>
       </span>
       <span>Print : </span>
        <span>yes<input type="radio" name="${id}.print" value="yes" checked/>
        no<input type="radio" name="${id}.print" value="no"/>
       </span>
       </fieldset>
      </s:iterator>
       
      <input class="change-permissions" type="submit" value="Change"/>
    </template>
   </div>
   
</div> 
 
 
</div>

 <form id="form">
  
    <div id="wizard">

      <div  title="Group">	
          
           <fieldset class="info">
		<legend>Group</legend>
	   
	   <label class="required">Name : </label>
		<input title="Name" name="name" type="text"/>
		<input name="createdBy" type="hidden" value="admin"/>
	
	</fieldset>
      </div>
      
       <div title="Permissions">	
          
           <fieldset class="info">
		<legend>Permissions</legend>
		
	
	</fieldset>
      </div>
   
      
    </div>
     
</form>

<script type="text/javascript" src="${js}/groups.js"></script>