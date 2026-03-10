const csrfToken = document.querySelector('meta[name="_csrf"]').content;
let filteredDepartment=null;
let currentPage=0;
let searchedQuery =null;
fetchAll();
const phpFormatter = new Intl.NumberFormat('en-US', {
                    style: 'currency',
                    currency: 'PHP',
                });

function fetchBasedOnEndPoints(url, method) {
fetchAverageSalary();
fetchAverageAge();
    fetch(url, {
        method: method,// Specify the HTTP method
        credentials: "include"

    }).then(response => response.json())
        .then(employees => {
            const tbody = document.getElementById('employees-list');
            tbody.innerHTML = ''; // clear existing rows if any
            employees.forEach(employee => {
                const row = document.createElement('tr');
                const csrfInput = document.createElement("input");
                csrfInput.type = "hidden";
                csrfInput.name = "_csrf";
                csrfInput.value = csrfToken;

                // 2. Apply to the object attribute
                const salaryCurrency = phpFormatter.format(employee.salary);

                row.innerHTML = `
                    <td>${employee.employeeId}</td>
                    <td>${employee.name}</td>
                    <td>${employee.dateOfBirth}</td>
                    <td>${employee.department}</td>
                    <td>${salaryCurrency}</td>
                    <td><button type="button" 
                    class="btn btn-outline-success" 
                        data-bs-toggle="modal" 
                        data-employee-id="${employee.employeeId}"
                        data-employee-name ="${employee.name}"
                        data-employee-dob ="${employee.dateOfBirth}"
                        data-employee-department="${employee.department}"
                        data-employee-salary="${employee.salary}"

                        data-bs-target="#editEmployeeModal">
                      Edit
                    </button></td>
                    <td><button class="btn btn-outline-danger" onClick="deleteItem(${employee.employeeId})">Delete</button></td>
                    `;

                const td = document.createElement('td');
                //td.appendChild(buttonEdit);
                row.appendChild(td);
                tbody.appendChild(row);

            });
        })

        .catch(error => {
            document.getElementById("errorMessage").innerText = error;

            console.error('Error loading employees:', error);
        });
}

async function fetchAverageSalary() {
    document.getElementById('average-salary').innerHTML = "";

    try {
        if(filteredDepartment!=null && filteredDepartment!=""){
            url = (`/api/employees/average_salary/dept=${filteredDepartment}`)
        }else
         url=`/api/employees/average_salary`

         const response = await fetch(url,{
            method: 'GET'});
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();
        console.log(data);
        const salaryCurrency = phpFormatter.format(data);

        document.getElementById('average-salary').innerHTML = `<b>Ave. Salary:</b> ${salaryCurrency}`;
    } catch (error) {
        console.error('Fetch error:', error);
        document.getElementById('dataOutput').textContent = 'Failed to load data.';
    }
}
async function fetchAverageAge() {

    try {
        if(filteredDepartment!=null && filteredDepartment!=""){
            url = (`/api/employees/average_age/dept=${filteredDepartment}`)
        }else
            url=`/api/employees/average_age`
        const response = await fetch(url,{
            method: 'GET'});
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();
        console.log(data);
        document.getElementById('average-age').innerHTML = `<b>Ave. Age:</b> ${data}`;
    } catch (error) {
        console.error('Fetch error:', error);
        document.getElementById('dataOutput').textContent = 'Failed to load data.';
    }
}
function deleteItem(employeeId) {
    if (confirm("Are you sure you want to delete this employee")) {
        fetch(`/api/employees/${employeeId}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (response.ok) {
                    console.log(`User ${employeeId} deleted successfully.`);
                    //Optionally, redirect or refresh the page
                    window.location.reload();
                } else {
                    // Handle errors (e.g., item not found, server error)
                    console.error('Failed to delete the employee.');
                }
            })
            .catch(error => {
                console.error('Network error:', error);
            });
    }
}

function fetchAll(page = 0) {
    currentPage = page;
    filteredDepartment=null
    searchedQuery =null;
    fetchBasedOnEndPoints(`/api/employees?page=${page}`, "GET");
}
let orderAge = "ASC";
let orderDepartment ="ASC"
function displayByAge(){
    fetchBasedOnEndPoints(`api/employees/sorted?sortBy=age&sortOrder=${orderAge}`)
    if (orderAge==="ASC"){
        orderAge = "DESC";
    }else{
        orderAge = "ASC";
    }
}
function displayByDepartment(){
    fetchBasedOnEndPoints(`api/employees/sorted?sortBy=department&sortOrder=${orderDepartment}`)
    if (orderDepartment==="ASC"){
        orderDepartment = "DESC";
    }else{
        orderDepartment = "ASC";
    }
}
document.getElementById("employees-filter").addEventListener("submit", function(event) {
    event.preventDefault(); // prevent page reload
    searchedQuery=null
    // Grab input values
    currentPage =0;
    document.getElementById('average-salary').innerHTML ="";
    document.getElementById('average-age').innerHTML ="";

    const department = document.getElementById("department").value;
    filteredDepartment=department;

    // Construct URL with query parameters
    const params = new URLSearchParams();
    if (department) params.append("department", department);

    const url = `/api/employees/filtered?${params.toString()}&page=${currentPage}`;
    fetchBasedOnEndPoints(url, "GET");
})
document.getElementById("search-bar").addEventListener("submit", function(event) {
    event.preventDefault(); // prevent page reload
    const query = document.getElementById("query").value;
    currentPage =0;
    filteredDepartment=null;
    searchedQuery=query;
    // Construct URL with query parameters
    const params = new URLSearchParams();
    if (query) params.append("query", query);

    const url = `/api/employees/search?${params.toString()}&page=${currentPage}`;
    fetchBasedOnEndPoints(url, "GET");

});

document.getElementById("nextBtn").addEventListener("click", () => {
currentPage+=1;
if ((filteredDepartment == null || filteredDepartment =="") &&(searchedQuery==null || searchedQuery=="") )
    fetchAll(currentPage);
    else if(filteredDepartment != null && filteredDepartment !=""){
        const url = `/api/employees/filtered?department=${filteredDepartment}&page=${currentPage}`;
        fetchBasedOnEndPoints(url, "GET");

    }else if(searchedQuery!=null && searchedQuery !=""){
        const url = `/api/employees/search?query=${searchedQuery}&page=${currentPage}`;
        fetchBasedOnEndPoints(url, "GET");

    }
});
document.getElementById("prevBtn").addEventListener("click", () => {
    if (currentPage > 0){
        currentPage-=1
    }
    if ((filteredDepartment == null || filteredDepartment =="") &&(searchedQuery==null || searchedQuery=="")){
         fetchAll(currentPage);
    }
    else if(filteredDepartment != null && filteredDepartment !=""){
         const url = `/api/employees/filtered?department=${filteredDepartment}&page=${currentPage}`;
         fetchBasedOnEndPoints(url, "GET");
    }else if(searchedQuery!=null && searchedQuery !=""){
         const url = `/api/employees/search?query=${searchedQuery}&page=${currentPage}`;
         fetchBasedOnEndPoints(url, "GET");
    }

});

const modal = document.getElementById("editEmployeeModal");

modal.addEventListener("show.bs.modal", event => {
    const btn = event.relatedTarget;
    console.log(btn.dataset);
    document.getElementById('edit-id').value = btn.dataset.employeeId;
    document.getElementById('edit-name').value = btn.dataset.employeeName;
    document.getElementById('edit-dateOfBirth').value = btn.dataset.employeeDob;
    document.getElementById('edit-department').value = btn.dataset.employeeDepartment;
    document.getElementById('edit-salary').value = btn.dataset.employeeSalary;
});