$(document).ready(function() {
    // Обработчик открытия модального окна редактирования
    $('#userEditDialog').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget);
        let modal = $(this);
        modal.find('#user-id').val(button.data('userid'));
        modal.find('#user-name').val(button.data('name'));
        modal.find('#user-username').val(button.data('username'));
        modal.find('#user-age').val(button.data('age'));
        modal.find('#user-email').val(button.data('email'));
        modal.find('#user-password').val(button.data('password'));

        // Установить роли
        let rolesString = button.data('roles');
        if (rolesString) {
            let rolesArray = rolesString.split(',');
            $('#user-role option').each(function() {
                $(this).prop('selected', rolesArray.includes($(this).val()));
            });
        }
    });

    // Обработка сохранения пользователя
    $('#save-user-button').click(function() {
        let modal = $('#userEditDialog');

        let userId   = modal.find('#user-id').val();
        let name     = modal.find('#user-name').val();
        let username = modal.find('#user-username').val();
        let age      = modal.find('#user-age').val();
        let email    = modal.find('#user-email').val();
        let password = modal.find('#user-password').val();

        // Получаем выбранные роли
        let selectedRoles = [];
        $('#user-role option:selected').each(function() {
            selectedRoles.push($(this).val());
        });

        $.ajax({
            url: '/api/admin/edit',
            type: 'POST',
            data: {
                id: userId,
                name: name,
                username: username,
                age: age,
                email: email,
                password: password,
                selectedRoles: selectedRoles // массив ролей
            },
            traditional: true, // сериализация массива
            success: () => {
                window.location.href = "/api/admin";
            },
            error: (err) => {
                alert('Error saving user');
            }
        });
    });

    // Обработчик открытия модального окна удаления
    $('#userDeleteDialog').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget);
        let modal = $(this);
        modal.find('#delete-user-id').val(button.data('userid'));
        modal.find('#delete-user-name').val(button.data('name'));
        modal.find('#delete-user-username').val(button.data('username'));
        modal.find('#delete-user-age').val(button.data('age'));
        modal.find('#delete-user-email').val(button.data('email'));

        // Установить роли
        let rolesString = button.data('roles');
        if (rolesString) {
            let rolesArray = rolesString.split(',');
            $('#delete-user-role option').each(function() {
                $(this).prop('selected', rolesArray.includes($(this).val()));
            });
        }
    });

    // Подтверждение удаления пользователя
    $('#confirm-delete-button').click(function() {
        let userId = $('#delete-user-id').val();

        $.ajax({
            url: '/api/admin/delete_user',
            type: 'GET',
            data: {id: userId},
            success: () => {
                window.location.href = "/api/admin";
            },
            error: () => {
                alert('Error deleting user');
            }
        });
    });

    // Обработка добавления нового пользователя через форму
    const form = document.getElementById('userForm');
    if (form) { // проверка, чтобы избежать ошибок, если формы нет на странице
        form.addEventListener('submit', function(e) {
            e.preventDefault(); // отменяем стандартную отправку формы

            const data = {
                name: document.getElementById('name').value,
                username: document.getElementById('username').value,
                age: parseInt(document.getElementById('age').value),
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                selectedRoles: [document.getElementById('role').value]
            };

            fetch('/api/admin/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(data)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Ошибка при добавлении пользователя');
                    }
                    return response.text();
                })
                .then(msg => {
                    alert(msg); // сообщение об успехе
                    window.location.href = "/api/admin";     // перенаправление после добавления
                })
                .catch(error => {
                    alert(error.message);
                });
        });
    }
});