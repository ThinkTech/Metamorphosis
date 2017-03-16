<h1>List Of Orders <a id="create" href="#form">Create</a></h1>
<form id="search">
	<span>Filter :</span>
	<select name="filter">
	   <option>Product</option>
	   <option>Customer</option>
	   <option>Created By</option>
	</select>
	<input name="value" type="text"  placeholder="Search..."/>
</form>
<div id="list">
<table>
 <thead>
    <tr>
       <th title="Product">Product</th>
       <th title="Price">Price</th>
       <th title="Quantity">Quantity</th>
       <th title="Amount">Amount</th>
       <th title="Customer">Customer</th>
       <th title="Created By">Created By</th>
    </tr>
 </thead>

<tbody>
   <template type="text/x-dust-template">
     {#.}
      <tr id="{id}">
   	        <td>{product}</td>
            <td>{price}</td>
   	    	<td>{quantity}</td>
            <td>{amount}</td>
            <td>{customer}</td>
            <td>{createdBy}</td>
   	    </tr>
     {/.}
   </template>
   
 </tbody>
  
</table>

</div>

<div id="selection"><span>select an order</span></div>

<div id="details">

<div id="tabs"> 
   <div title="Info 1">
     <template  type="text/x-dust-template">

       <fieldset>
		<span>Product : </span>
		<span>{product}</span>
		
		<span>Price : </span>
		<span>{price}</span>
	
       <span>Quantity : </span>
		<span>{quantity}</span>
       
      <span>Amount : </span>
	  <span>{amount}</span>
	
      <span>Customer : </span>
	  <span>{customer}</span>
		
      	
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
      
      <div  title="Info 1">	
          
           <fieldset class="info">
		<legend>Info 1</legend>
		
		<label class="required">Product : </label>
		<input  title="Product" name="product" type="text"/>
		
		<label class="required">Quantity : </label>
		<input title="Quantity" name="quantity" type="text"/>
		
		<label class="required">Customer : </label>
		<input title="Customer" name="customer" type="text"/>
		
		
	   <input name="createdBy" type="hidden" value="tetiana"/>
	
	</fieldset>
      </div>
      
       <div title="Info 2">	
          
           <fieldset class="info">
		<legend>Info 2</legend>
		
		  <label class="required">Field 1 : </label>
		<input title="" name="field1" type="text"/>
		
		  <label class="required">Field 2 : </label>
		<input title="" name="field2" type="text"/>
	
	</fieldset>
      </div>
      
    </div>
     
</form>