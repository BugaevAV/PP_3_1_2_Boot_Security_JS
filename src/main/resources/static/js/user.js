async function getUserTable() {
    let table = $('#userTable tbody');
    table.empty();

    let response = await fetch('/api/v1/user_information');
    if (response.ok) {
        let user = await response.json();
        let html = `
                  <tr>
                   <td>${user.username}</td>
                   <td>${user.email}</td>
                   <td>${user.password}</td>
                   <td>${user.roleNames.join(', ')}</td>
                 </tr>`
        table.append(html);
    }
}

getUserTable();
