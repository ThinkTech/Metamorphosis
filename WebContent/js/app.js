app.apiURL = "http://env-4347792.mircloud.host/";

window.addEventListener('offline', () => {
	$("<div id='offline'><span>You are currently offline</span></div>").appendTo($("body"));
	app.wait();
});

window.addEventListener('online', () => {
	$("div#offline").remove();
	app.release();
});

page.table = {};

page.table.paginate = function() {
	$("table").unbind("repaginate").each(function() {
		var $table = $(this);
		$(".pager").remove();
	    var currentPage = 0;
	    var numPerPage = 12;
	    $table.bind('repaginate', function() {
	        $table.find('tbody tr').hide().slice(currentPage * numPerPage, (currentPage + 1) * numPerPage).show();
	    });
	    $table.trigger('repaginate');
	    var numRows = $table.find('tbody tr').length;
	    if(numRows > numPerPage) {
		    var numPages = Math.ceil(numRows / numPerPage);
		    var $pager = $('<div class="pager"></div>').attr("id","pager"+$table.parent().attr("id"));
		    for (var page = 0; page < numPages; page++) {
		        $('<span class="page-number"></span>').text(page + 1).bind('click', {
		            newPage: page
		        }, function(event) {
		            currentPage = event.data['newPage'];
		            $table.trigger('repaginate');
		            $(this).addClass('active').siblings().removeClass('active');
		            $table.parent().focus();
		            return false;
		        }).appendTo($pager);
		    }
		    $pager.insertAfter($table.parent()).find('span.page-number:first').addClass('active');
		}
	    head.load("js/sortable.js", function() {
	    	sorttable.makeSortable($table[0]);
	    });
	});
};

page.table.init = function() {
	var tbody = $("#list tbody");
	var rows = $("tr",tbody).length; 
	if(!rows) {
		tbody.append("<tr class='empty'><td valign='top' colspan='"+$("th").length+"'>no record found</td></tr>");
		$("<div class='row-count'/>").html("0 records").insertAfter("#list");
	}else {
		page.table.paginate();
		$("<div class='row-count'/>").html(rows + " records").insertAfter("#list").fadeIn();
	}
	$("#search input").focus().val($("#search input").val());
	$("a.refresh-16").attr("href",window.location.href);
};

page.tabs = {};

page.tabs.init = function() {
	var tabs = $("#tabs").addClass("tab_container");
	var ul = $('<ul class="tabs"></ul>').insertBefore(tabs);
	$.each($("> div",tabs),function(index, element){
		  var div= $(element).attr("id","tab"+index).addClass("tab_content").hide();
		  var h2 = $("<h2>"+div.attr("title")+"</h2>").attr("title",div.attr("title"));
		  var li = $("<li/>").attr("rel",div.attr("id")).html(h2);
		  li.click(function() {
				var parent = $(this).parent();
				$("li",parent).removeClass("active");
				$(this).addClass("active");
				var activeTab = $(this).attr("rel"); 
				$("#"+activeTab).parent().find(".tab_content").hide();
				$("#"+activeTab).fadeIn(); 
		  }).appendTo(ul);
	});
	$("li:first-child",ul).addClass("active");
	$("div:first-child",tabs).fadeIn();
};

page.highlight = function() {
	var array = window.location.pathname.split( '/' );
	var path = "";
	for( var i = 0;i<array.length;i++) {
		path += array[i];
		if(array[i]) $('a[href$='+array[i]+"]").addClass('active');
	}
	if(path && array[1]) $('a[href$='+array[1]+"]").addClass('active');
	if($("aside a.active").length>1) $("aside a.active:first").removeClass("active");
};

page.table.init = entity => {
	page.table.url = app.apiURL+entity+"s";
	page.table.message = "no "+entity;
	sorttable.makeSortable($("table")[0]);
	const list = $("#list").attr("tabindex","1").attr("oncontextmenu","return false;");
	const menu = $("<div id='contextmenu' tabindex='0'></div>").insertAfter(list);
	$("<a class='icon-16' tabindex='1'>Select</a>").addClass(entity+"-16").appendTo(menu).click(() => page.table.selectedRow.click());
	$("<a class='edit-16' tabindex='2'>Edit</a>").appendTo(menu).click(() => page.table.editRow(page.table.selectedRow));
	$("<a class='delete-16' tabindex='3'>Delete</a>").appendTo(menu).click(() => confirm(() => page.table.removeRow(page.table.selectedRow)));
	$("<a class='print-16' tabindex='4'>Print</a>").appendTo(menu).click(() => page.print(page.table.url+"/"+page.table.selectedRow.attr("id")));
	$("<a class='pdf-16' tabindex='5'>PDF</a>").appendTo(menu).click(() => page.pdf(page.table.url+"/"+page.table.selectedRow.attr("id")));
	$("a",menu).attr("oncontextmenu","return false;");
	menu.on('keydown', function (e) {
		const element = $("a.focus",menu);
		switch(e.keyCode) {
		    case 27:
		    	menu.hide();
		    	list.focus();
		    	break;
            case 40:
            	if(element.length && element.next().length) {
            		$("a",menu).removeClass("focus");
            		element.next().addClass("focus");
            	}else {
            		$("a",menu).removeClass("focus");
            		$("a:first-child",menu).addClass("focus");
            	}
                break;
            case 38:
            	if(element.length && element.prev().length) {
            		$("a",menu).removeClass("focus");
            		element.prev().addClass("focus");
            	}else {
            		$("a",menu).removeClass("focus");
            		$("a:last-child",menu).addClass("focus");
            	}
                break;
            case 13:
            	element.click();
                break;
        }
    	return false;
        
    });
	list.on('keydown', function (e) {  
		if(!page.table.selectedRow) return;
    	switch(e.keyCode) {
            case 40:
            	const next = page.table.selectedRow.next();
            	if(next.length) {
            		page.table.selectedRow.removeClass('focus');
            		page.table.selectedRow = next.addClass('focus');
            		$(".page-number").eq(Math.floor(page.table.selectedRow.index() / 7)).click();
            	}
            	return false;
            case 38:
            	const prev = page.table.selectedRow.prev();
            	if(prev.length) {
            		page.table.selectedRow.removeClass('focus');
            		page.table.selectedRow = prev.addClass('focus');
            		$(".page-number").eq(Math.floor(page.table.selectedRow.index() / 7)).click();
            	}
            	return false;
            case 46:
            	confirm(() => page.table.removeRow(page.table.selectedRow));
            	return false;
            case 13:
            	page.table.selectedRow.click();
            	return false;
            case 93:
            	var left =  window.innerWidth-500;
        		if(left>window.innerWidth-100) left = window.innerWidth -200;
        		$("a",menu).removeClass("focus");
        		menu.show().css({top : page.table.selectedRow.position().top+10, left:left});
        		page.table.keyboard = true;
        		setTimeout(() => menu.focus(), 500);
        		return false;
            case 80:
            	if(e.ctrlKey) page.print(page.table.url+"/"+page.table.selectedRow.attr("id"));
            	return false;
            case 69:
            	if(e.ctrlKey) page.table.editRow(page.table.selectedRow);
            	return false;
            case 71:
            	if(e.ctrlKey) page.pdf(page.table.url+"/"+page.table.selectedRow.attr("id"));
            	return false;
        }
        
    }).click(() => {
    	page.table.selectedRow = page.table.selectedRow ? page.table.selectedRow.addClass('focus') : $("tbody tr:first-child").addClass('focus');
    	menu.hide();
    	return false;
    }); 
};

page.table.render = entity => {
	page.table.init(entity);
	app.get(page.table.url, entities => page.render($("tbody"), entities, page.table.display));
};

page.table.display = rows => {
	if(!rows.length) {
		$("tbody").append("<tr class='empty'><td valign='top' colspan='"+$("th").length+"'>"+page.table.message+"</td></tr>");
		$("#details").hide();
		$("#selection").hide();
	} else {
		$.each(rows,(i,row) => $("td:first-child",row).addClass("icon-16").addClass(page.form.entity.toLowerCase()+"-16"));
		page.table.bind(rows);
		page.table.paginate();
		page.table.details.hide(true);
	}
};

page.table.bind = element => {
	element.click(function(event){
		const row = $(this);
		app.get(page.table.url+"/"+row.attr("id"), entity => page.table.details.show(entity,row));
		return false;
	}).contextmenu(function(event){
		if(!page.table.keyboard) {
			$("tr.focus").removeClass("focus");
			const row = $(this).addClass("focus");
			var left = event.pageX;
			if(left>window.innerWidth-100) left = window.innerWidth -200;
			$("#contextmenu").show().css({top : row.position().top+10, left:left}).focus();
			page.table.selectedRow = row;
		}
	    return page.table.keyboard = false;
	}).attr("oncontextmenu","return false;");
	
};

page.table.removeRow = row => {
	app.delete(page.table.url+"/"+row.attr("id"), () => {
		const next = row.next();
		next.length ? next.click() : row.prev().click();
		row.remove();
		if(!$("tbody tr").length) {
			$("tbody").append("<tr class='empty'><td  valign='top' colspan='"+$("th").length+"'>"+page.table.message+"</td></tr>");
			$("#details").hide();
			$("#selection").hide();
		}else {
			const number = $(".page-number.active").text();
			page.table.paginate();
			if(number <= $(".page-number").length) {
			  $(".page-number").eq(number-1).click();
			} else if(number > $(".page-number").length) {
				$(".page-number").eq(number-2).click();
			}
		}
	});
};

page.table.addRow = entity => {
	$("tr.empty").remove();
	page.render($("tbody"), [entity], true, row => {
		$("td:first-child",row).addClass("icon-16").addClass(page.form.entity.toLowerCase()+"-16");
		page.table.paginate();
		page.table.bind(row);
		row.attr("id","1455555");
		page.table.details.show(entity,row);
		$("span.page-number:last").click();
	});
};

page.table.editRow = row => {
	app.get(page.table.url+"/"+row.attr("id"),entity => {
		page.deserialize($("#form"),entity);
		page.form.edit(entity);
		$('#form h1').html("Update "+page.form.entity+": Informations");
		page.edit = true;
		page.table.details.show(entity,row);
		$(".page-number").eq(Math.floor(row.index() / 7)).click();
	});
};

page.table.updateRow = entity => {
	const container = $("<div/>");
	page.render($("tbody"), [entity] ,false, container, row => {
		page.table.selectedRow.html($("tr",container).html());
		$("td:first-child",page.table.selectedRow).addClass("icon-16").addClass(page.form.entity.toLowerCase()+"-16");
		page.table.details.show(entity,page.table.selectedRow);
	});
};

page.deserialize = (form, entity) => {
    $.each(entity, function(key, value){
      $('[name='+key+']', form).val(value);
      if(value === Object(value)) {
    	 $.each(value, function(k, val){
    	    $('[name='+key+"\\["+k+'\\]]', form).val(val);
    	 });
      }
     });
};


page.table.details = {};

page.table.details.show = (entity,row) => {
	$.each($("#tabs > div"),(i, element) => page.render($(element),entity));
	const h2 = $("#details > h2").html("Details "+page.form.entity + " : " +page.table.details.title(entity));
	$("<a title='Edit' class='edit-16'></a>").appendTo(h2).click(() => page.table.editRow(row));
	$("<a title='Delete' class='delete-16'></a>").appendTo(h2).click(() => confirm(() => page.table.removeRow(row)));
	$("<a title='Imprimer' class='print-16'></a>").appendTo(h2).click(() => page.print(page.table.url+"/"+row.attr("id")));
	$("<a title='PDF' class='pdf-16'></a>").appendTo(h2).click(() => page.pdf(page.table.url+"/"+row.attr("id")));
	$("#details > h2 a").click(() => false);
	page.table.details.hide(false);
	$("tr.active").removeClass("active");
	$("tr.focus").removeClass("focus");
	page.table.selectedRow = row.addClass("active focus");
	$("#list").focus();
	
};

page.table.details.hide = hide => {
	if(hide) {
		$("#details").hide();
		$("#selection").slideDown(500);
	}else {
		$("#selection").hide();
		$("#details").show();
	}
};

page.search = {};

page.search.init = () => {
	$('#search input').val("").focus();
	$('#search').submit(function(){;
	     if(!$('input',this).val().trim()) {
	    	 app.get(page.table.url, entities => page.render($("tbody"), entities, page.table.display));
	     }else {
	    	 const data = $(this).serialize();
		 	 app.post(page.table.url+"/search",data, entities => page.render($("tbody"), entities, page.table.display));
	     }
		 return false;
	});
};

page.form = {};

page.form.submit = () => {
	if(!page.edit) {
		 app.post(page.table.url, $("#form").serialize(), page.table.addRow);
	}else {
		 app.put(page.table.url, $("#form").serialize(), page.table.updateRow);
	}
	page.edit = false;
};

page.form.create = () => {
	page.edit = false;
	$('#form h1').html("Create "+page.form.entity +" : Informations");
	$('#form')[0].reset();
	$("#contextmenu").hide();
	return false;
};

page.tabs = {};

page.tabs.init = entity => {
	var tabs = $("#details #tabs").addClass("tab_container");
	const ul = $('<ul class="tabs"></ul>').insertBefore(tabs);
	$.each($("> div",tabs),(index, element) => {
		  const div= $(element).attr("id","tab"+index).addClass("tab_content").hide();
		  const h2 = $("<h2>"+div.attr("title")+"</h2>").attr("title",div.attr("title"));
		  const li = $("<li/>").attr("rel",div.attr("id")).html(h2);
		  li.click(function() {
				const parent = $(this).parent();
				$("li",parent).removeClass("active");
				$(this).addClass("active");
				const activeTab = $(this).attr("rel"); 
				$("#"+activeTab).parent().find(".tab_content").hide();
				$("#"+activeTab).fadeIn(); 
		  }).appendTo(ul);
	});
	$('<h2 class="icon-16"></h2>').addClass(entity+"-16").insertBefore(ul);
	$("li:first-child",ul).addClass("active");
	$("div:first-child",tabs).show();
};

const module = {};

module.init = (entity,title) => {
	app.ready(() => {
		if(!$("#list").length) return;
		page.form.entity = entity[0].toUpperCase() + entity.slice(1);
		page.form.init();
		$('#create').click(() => page.form.create());
		page.table.render(entity);
		page.search.init();
		page.table.details.title = title ? title : entity => {
			const names = Object.getOwnPropertyNames(entity);
			return entity[names[1]] + " " + entity[names[2]];
		};
		$("section > h1").addClass("icon-32").addClass(entity+"-32");
		$('<h1 class="icon-32"></h1>').addClass(entity+"-32").insertBefore("#wizard");
		$("#selection span").addClass("icon-16").addClass(entity+"-16");
		page.tabs.init(entity);
		$("body").on('keydown',event => {
			switch(event.keyCode) {
			  case 67:
            	if(event.ctrlKey) $('#create').click();
            	break;
			  case 83:
				if(event.ctrlKey) {
					page.table.selectedRow = page.table.selectedRow ? page.table.selectedRow.addClass('focus') : $("tbody tr:first-child").addClass('focus');
					$("#list").focus();
					return false;
				}
				break;
			  case 70:
				if(event.ctrlKey) {
					$("#search input").select().focus();
					return false;
				}
				break;
	        }
	    });
	});
};

app.ready(() => {
	
	$("body").click(() => {
		$(".focus").removeClass("focus");
		$("#contextmenu").hide()
	});
});