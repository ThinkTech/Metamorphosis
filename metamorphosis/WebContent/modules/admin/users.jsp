<%@ taglib prefix="s" uri="/struts-tags"%>
<h1>List Of Users <a id="create" href="#form">Create</a></h1>
<form id="search">
	<span>Filter :</span>
	<select name="filter">
	   <option>First Name</option>
	   <option>Last Name</option>
	   <option>Login</option>
       <option>Email</option>
       <option>Group</option>
       <option>Role</option>
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
       <th title="Login">Login</th>
       <th title="Email">Email</th>
       <th title="Group">Group</th>
        <th title="Role">Role</th>
       <th title="Created By">Created By</th>
    </tr>
 </thead>

<tbody>
   <template type="text/x-dust-template">
     {#.}
      <tr id="{id}">
   	        <td>{firstName}</td>
   	    	<td>{lastName}</td>
   	    	<td>{login}</td>
   	    	<td>{email}</td>
   	    	<td>{group}</td>
            <td>{role}</td>
   	    	<td>{createdBy}</td>
   	    </tr>
     {/.}
   </template>
   
 </tbody>
  
</table>

</div>

<div id="selection"><span>select a user</span></div>

<div id="details">

<div id="tabs"> 
   <div  title="Personal Informations">
     <template  type="text/x-dust-template">

       <fieldset>
		<span>First Name : </span>
		<span>{firstName}</span>
		
		<span>Last Name : </span>
		<span>{lastName}</span>
		
		<span>Login :</span>
		<span>{login}</span>
		
        <span>email :</span>
		<span>{email}</span>

        <span>group :</span>
		<span>{group}</span>

         <span>role :</span>
		<span>{role}</span>

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
   
   <div  title="Permissions">
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

      <div  title="User">	
          
           <fieldset class="info">
		<legend>User</legend>
		
		<label class="required">First Name : </label>
		<input title="First Name" name="firstName" type="text"/>
		
		<label class="required">Last Name : </label>
		<input title="Last Name" name="lastName" type="text"/>
		
		<label class="required">Login :</label>
		<input title="Login" name="login" type="text"/>
		
		<label class="required">Password :</label>
		<input title="Password" name="password" type="text"/>
		
		<label class="required">Group :</label>
		<input title="Group" name="group" type="text"/>
		
		<label class="required">Role :</label>
		<input title="Role" name="role" type="text"/>
		
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
		
		<input name="createdBy" type="hidden" value="admin"/>
	
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

<script type="text/javascript" src="${js}/users.js"></script>