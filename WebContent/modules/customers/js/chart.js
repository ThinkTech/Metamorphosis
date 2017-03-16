function getCustomers() {
	var ctx = document.getElementById("customers");
	var data = {
		    labels: [
		        "Total",
		        "Customers"
		    ],
		    datasets: [
		        {
		            data: [15000, 10000],
		            backgroundColor: [
		                "#FF6384",
		                "#36A2EB",
		                "#FFCE56"
		            ],
		            hoverBackgroundColor: [
		                "#FF6384",
		                "#36A2EB",
		                "#FFCE56"
		            ]
		        }]
		};
	var myPieChart = new Chart(ctx,{
	    type: 'pie',
	    data: data
	});
};

function getPaidCustomers() {
	var ctx = document.getElementById("paid-customers");
	var data = {
		    labels: [
		        "Total",
		        "Paid Customers"
		    ],
		    datasets: [
		        {
		            data: [10000, 8000],
		            backgroundColor: [
		                "#FF6384",
		                "#36A2EB",
		                "#FFCE56"
		            ],
		            hoverBackgroundColor: [
		                "#FF6384",
		                "#36A2EB",
		                "#FFCE56"
		            ]
		        }]
		};
	var myPieChart = new Chart(ctx,{
	    type: 'pie',
	    data: data
	});
};

document.addEventListener("DOMContentLoaded", () => {
	getCustomers();
	getPaidCustomers();
});