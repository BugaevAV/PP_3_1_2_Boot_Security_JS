$('DOMContentLoaded', async function (){
    await getAdminTable();
    getGeneralModal();
    addNewUser();
    personalInfoTable()
})

const urlPrefix = '/api/v1';
const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
};

async function getAdminTable() {
    let table = $('#adminTable tbody');
    table.empty();

    let response = await fetch(urlPrefix + '/get_all_users');
    if (response.ok) {
        let users = await response.json();
        users.forEach(user => {
            let html = `
              <tr>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td style="word-wrap: break-word;min-width: 160px;max-width: 160px;">${user.password}</td>
                <td>${user.roleNames.join(', ')}</td>
                <td>
                  <button type="button" data-userid="${user.id}" data-action="edit" class="btn btn-info" 
                  data-toggle="modal" data-target="#modalGeneral">Edit</button>
                </td>
                <td>
                  <button type="button" data-userid="${user.id}" data-action="delete" class="btn btn-secondary"
                   data-toggle="modal" data-target="'#modalGeneral">Delete</button>
                </td>
              </tr>`
            table.append(html);
        })
    }

    $('#adminTable').find('button').on('click', (event) => {
        let generalModal = $('#modalGeneral');
        let targetButton = $(event.target);
        let buttonUserId = targetButton.attr('data-userid');
        let buttonAction = targetButton.attr('data-action');
        generalModal.attr('data-userid', buttonUserId);
        generalModal.attr('data-action', buttonAction);
        generalModal.modal('show')
    })
}

async function getGeneralModal() {
    $('#modalGeneral').modal({
        keyboard: true,
        backdrop: "static",
        show: false
    }).on("show.bs.modal", (event) => {
        let thisModal = $(event.target);
        let userid = thisModal.attr('data-userid');
        let action = thisModal.attr('data-action');
        switch (action) {
            case 'edit':
                editUser(thisModal, userid);
                break;
            case 'delete':
                deleteUser(thisModal, userid);
                break;
        }
    }).on("hidden.bs.modal", (e) => {
        let thisModal = $(e.target);
        thisModal.find('.modal-title').html('');
        thisModal.find('.modal-body').html('');
        thisModal.find('.modal-footer').html('');
    })
}

async function editUser(modal, id) {
    let user = (await fetch(urlPrefix + '/get_user/' + id)).json();
    let rolesExist = await (await fetch(urlPrefix + '/get_roles_exist')).json().then(
        roles => roles
    );

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button  class="btn btn-success" id="editButton">Edit</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let roles = '';
        rolesExist.forEach(role => {
            if (user.roleNames.includes(role)) {
                roles += `<option selected>${role}</option>`;
            } else {
                roles += `<option>${role}</option>`;
            }
        })
        let bodyForm = `
          <div class="row">
            <div class="col-3"></div>
                <div class="h6 col-6">
                <form class="form-group" id="editUser">
                    <label for="id">Id</label>
                    <input class="form-control" type="text" id="id" name="id" value="${user.id}" disabled><br>
                    <label for="username">Name</label>
                    <input class="form-control" type="text" id="username" value="${user.username}"><br>
                    <label for="email">Email</label>
                    <input class="form-control" type="text" id="email" value="${user.email}"><br>
                    <label for="password">Password</label>
                    <input class="form-control" type="text" id="password" value="${user.password}"><br>
                    <label for="roles">Roles</label>
                    <select class="form-control" id="roles" size="2" multiple required>
                      ${roles}
                    </select>
                </form>
              </div>
          </div>
        `;
        modal.find('.modal-body').append(bodyForm);
    })

    $("#editButton").on('click', async () => {
        let id = modal.find("#id").val().trim();
        let username = modal.find("#username").val().trim();
        let email = modal.find("#email").val().trim();
        let password = modal.find("#password").val().trim();
        let roleNames = modal.find("#roles").val();
        let data = {
            id: id,
            username: username,
            email: email,
            password: password,
            roleNames: roleNames
        }
        const response = await fetch(urlPrefix + '/update_user/' + id, {
            method: 'PATCH', headers: headers, body: JSON.stringify(data)
        })

        if (response.ok) {
            getAdminTable();
            console.log('функция выполнена edit')
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="messageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}

async function deleteUser(modal, id) {
    let user = (await fetch(urlPrefix + '/get_user/' + id)).json();
    let rolesExist = await (await fetch(urlPrefix + '/get_roles_exist')).json().then(
        roles => roles
    );

    modal.find('.modal-title').html('Delete user');

    let deleteButton = `<button  class="btn btn-danger" id="deleteButton">Delete</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(deleteButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let roles = '';
        rolesExist.forEach(role => {
            if (user.roleNames.includes(role)) {
                roles += `<option selected>${role}</option>`;
            } else {
                roles += `<option>${role}</option>`;
            }
        })
        let bodyForm = `
          <div class="row">
            <div class="col-3"></div>
                <div class="h6 col-6">
                <form class="form-group" id="deleteUser">
                    <label for="id">Id</label>
                    <input class="form-control" type="text" id="id" name="id" value="${user.id}" disabled><br>
                    <label for="username">Name</label>
                    <input class="form-control" type="text" id="username" value="${user.username}" disabled><br>
                    <label for="email">Email</label>
                    <input class="form-control" type="text" id="email" value="${user.email}" disabled><br>
                    <label for="password">Password</label>
                    <input class="form-control" type="text" id="password" value="${user.password}" disabled><br>
                    <label for="roles">Roles</label>
                    <select class="form-control" id="roles" size="2" multiple disabled>
                      ${roles}
                    </select>
                </form>
              </div>
          </div>
        `;
        modal.find('.modal-body').append(bodyForm);
    })

    $("#deleteButton").on('click', async () => {
        const response = await fetch(urlPrefix + '/delete_user/' + id, {
            method: 'DELETE', headers: headers
        })

        if (response.ok) {
            getAdminTable();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="messageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}

async function addNewUser() {
    const newUserForm = document.getElementById("newUserForm");
    newUserForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        let name = newUserForm.querySelector('#usernameId').value.trim();
        let email = newUserForm.querySelector('#emailId').value.trim();
        let password = newUserForm.querySelector('#passId').value.trim();
        let allRoles = document.getElementById('rolesId');
        let rolesList = [];
        for(let option of allRoles.selectedOptions) {
            rolesList.push(option.value)
        }

        let data = {
            username: name,
            email: email,
            password: password,
            roleNames: rolesList
        }

        await fetch(urlPrefix + '/add_new_user', {
            method: 'POST', headers: headers, body: JSON.stringify(data)
        });
        newUserForm.reset();

        await getAdminTable();

        location.reload();
    })
}

async function personalInfoTable() {
    $('#v-pills-profile-tab').click(async () => {
        let table = $('#adminPersonalInfo tbody');
        table.empty()

        let response = await fetch(urlPrefix + '/personal_info')
        if (response.ok) {
            let admin = await response.json();
            let html = `
              <tr>
               <td>${admin.username}</td>
               <td>${admin.email}</td>
               <td>${admin.password}</td>
               <td>${admin.roleNames.join(', ')}</td>
             </tr>`
            table.append(html);
        }
    })
}
