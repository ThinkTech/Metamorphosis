<h1>List Of Customers <a id="create" href="#form">Create</a></h1>
<form id="search">
	<span>Filter :</span>
	<select name="filter">
	   <option>First Name</option>
	   <option>Last Name</option>
       <option>Date Of Birth</option>
       <option>Country</option>
       <option>Created By</option>
	</select>
	<input name="value" type="text"  placeholder="Search..."/>
</form>

<div id="list">
<table>
 <thead>
    <tr>
       <th title="First Name">First Name</th>
       <th title="Last Name">Last Name</th>
       <th title="Profession">Profession</th>
       <th title="Date Of Birth">Date Of Birth</th>
       <th title="Country">Country</th>
       <th title="Created By">Created By</th>
    </tr>
 </thead>

<tbody>
   <template type="text/x-dust-template">
     {#.}
      <tr id="{id}">
   	        <td>{firstName}</td>
   	    	<td>{lastName}</td>
   	    	<td>{profession}</td>
   	    	<td>{dateOfBirth}</td>
   	    	<td>{country}</td>
   	    	<td>{createdBy}</td>
   	    </tr>
     {/.}
   </template>
   
 </tbody>
  
</table>

</div>

<div id="selection"><span>select a customer</span></div>

<div id="details">

<div id="tabs"> 
   <div title="Personal Informations">
     <template  type="text/x-dust-template">

       <fieldset>
		<span>First Name : </span>
		<span>{firstName}</span>
		
		<span>Last Name : </span>
		<span>{lastName}</span>
		
		<span>Profession :</span>
		<span>{profession}</span>
		
		<span>Date Of Birth :</span>
		<span>{dateOfBirth}</span>
		
		<span>Country :</span>
		<span>{country}</span>
		
		<span>Sex :</span>
		<span>{sex}</span>
		
		</fieldset>
		
		<fieldset>
		
		<span>Telephone :</span>
		<span>{telephone}</span>
		
		<span>Mobile :</span>
		<span>{mobile}</span>
	   
	    <span>Email :</span>
		<span>{email}</span>
	
	  </fieldset>

     </template>
     
   </div>
   <div title="Contact">
    <template  type="text/x-dust-template">
      <fieldset>
		<span>First Name : </span>
		<span>{contact.firstName}</span>
		
		<span>Last Name : </span>
		<span>{contact.lastName}</span>
		
		<span>Profession :</span>
		<span>{contact.profession}</span>
		
		<span>Date Of Birth :</span>
		<span>{contact.dateOfBirth}</span>
		
		<span>Country :</span>
		<span>{contact.country}</span>
		
		<span>Sex :</span>
		<span>{contact.sex}</span>
		
		<span>Relation :</span>
		<span>{contact.relation}</span>
		
		</fieldset>
		
		<fieldset>
		
		<span>Telephone :</span>
		<span>{contact.telephone}</span>
		
		<span>Mobile :</span>
		<span>{contact.mobile}</span>
	   
	    <span>Email :</span>
		<span>{contact.email}</span>
	
	  </fieldset>

   </template>
   
   </div>
   
</div> 
 
 
</div>

 <form id="form"> 
   
    <div id="wizard">
    
      <div title="Customer">	
          
           <fieldset class="info">
		<legend>Customer</legend>
		
		<label class="required">First Name : </label>
		<input title="First Name" name="firstName"  type="text"/>
		
		<label class="required">Last Name : </label>
		<input title="Last Name" name="lastName" type="text"/>
		
		<label class="required">Profession :</label>
		<input title="Profession" name="profession" type="text"/>
		
		<label>Date Of Birth :</label>
		<input title="Date Of Birth" name="dateOfBirth" type="text"/>
		
		<label class="required">Sex :</label>
		<select title="Sex" name="sex">
			 <option>Male</option>
		 	<option>Female</option>
		</select>
		
		<label class="required">Country :</label>
		<select title="Country" name="country">
			 <option>USA</option>
		</select>
		
		<label>Telephone :</label>
		<input title="Telephone" name="telephone" type="text"/>
		
		<label class="required">Mobile :</label>
		<input title="Mobile" name="mobile" type="text"/>
	   
	    <label>Email :</label>
		<input title="Email" name="email" type="text"/>
		
		<input name="createdBy" type="hidden" value="tetiana"/>
	
	</fieldset>
      </div>
      
       <div title="Contact">	
          
           <fieldset class="info">
		<legend>Contact</legend>
		
		<label class="required">First Name : </label>
		<input title="First Name" name="contact[firstName]" type="text"/>
		
		<label class="required">Last Name : </label>
		<input title="Last Name" name="contact[lastName]" type="text"/>
		
		<label class="required">Profession :</label>
		<input title="Profession" name="contact[profession]" type="text"/>
		
		<label>Date Of Birth :</label>
		<input title="Date Of Birth" name="contact[dateOfBirth]" type="text"/>
		
		<label class="required">Sex :</label>
		<select title="Sex" name="contact[sex]">
			 <option>Male</option>
		 	<option selected="selected">Female</option>
		</select>
		
		<label class="required">Country :</label>
		<select title="Country" name="contact[country]">
			 <option>USA</option>
		</select>
		
		<label class="required">Relation :</label>
		<select title="Relation"  name="contact[relation]">
		     <option>Wife</option>
			 <option>Husband</option>
			 <option>Parent</option>
			 <option>Friend</option>
			 <option>Other</option>
		</select>
		
		<label>Telephone :</label>
		<input title="Telephone" name="contact[telephone]" type="text"/>
		
		<label class="required">Mobile :</label>
		<input title="Mobile" name="contact[mobile]" type="text"/>
	   
	    <label>Email :</label>
		<input title="Email" name="contact[email]" type="text"/>
	
	</fieldset>
      </div>
      
    </div>
     
</form>