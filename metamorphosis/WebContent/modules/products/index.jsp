<h1>List Of Products <a id="create" href="#form">Create</a></h1>
<form id="search">
	<span>Filter :</span>
	<select name="filter">
	   <option>Name</option>
	   <option>Price</option>
	   <option>Created By</option>
	</select>
	<input name="value" type="text"  placeholder="Search..."/>
</form>
<div id="list">
<table>
 <thead>
    <tr>
       <th title="Name">Name</th>
       <th title="Price">Price</th>
       <th title="Created By">Created By</th>
    </tr>
 </thead>

<tbody>
   <template type="text/x-dust-template">
     {#.}
      <tr id="{id}">
   	        <td>{name}</td>
   	    	<td>{price}</td>
            <td>{createdBy}</td>
   	    </tr>
     {/.}
   </template>
   
 </tbody>
  
</table>

</div>

<div id="selection"><span>select a product</span></div>

<div id="details">

<div id="tabs"> 
   <div title="Info 1">
     <template  type="text/x-dust-template">

       <fieldset>
		<span>Name : </span>
		<span>{name}</span>
		
		<span>Price : </span>
		<span>{price}</span>
	
		
		</fieldset>
		

     </template>
     
   </div>
   <div title="Info 2">
    <template  type="text/x-dust-template">
      <fieldset>
		
		</fieldset>
		

   </template>
   
   </div>
   
</div> 
 
 
</div>

 <form id="form">
   
    <div id="wizard">
      
      <div title="Info 1">	
          
           <fieldset class="info">
		<legend>Info 1</legend>
		
		<label class="required">Name : </label>
		<input title="Name" name="name" type="text"/>
		
		<label class="required">Price : </label>
		<input title="Price" name="price" type="text"/>
		
	   <input name="createdBy" type="hidden" value="tetiana"/>
	
	</fieldset>
      </div>
      
       <div title="Info 2">	
          
           <fieldset class="info">
		<legend>Info 2</legend>
		
	    <label class="required">Field 1 : </label>
		<input title="Field 1" name="field1" type="text"/>
		
		<label class="required">Field 2 : </label>
		<input title="Field 2" name="field2" type="text"/>
	
	</fieldset>
      </div>
      
    </div>
     
</form>