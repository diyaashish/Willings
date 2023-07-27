const modal = document.getElementById('modal');
const closeButton = document.getElementById('closeBtn');

closeButton.addEventListener('click', function() {
  modal.style.display = 'none';
});

const modalButton = document.getElementById('modBtn');

modalButton.addEventListener('click', function() {
  modal.style.display = 'block';
});

// Todo List
const todoInput = document.querySelector('.toDo');
const todoList = document.querySelector('.todo-list');

function addTodoToList(todoText) {
  const todoItem = document.createElement('div');
  todoItem.classList.add('todo-item');

  const checkbox = document.createElement('input');
  checkbox.type = 'checkbox';
  checkbox.classList.add('check');
  todoItem.appendChild(checkbox);

  const todoTextElement = document.createElement('span');
  todoTextElement.textContent = todoText;
  todoItem.appendChild(todoTextElement);

  const removeButton = document.createElement('span');
  removeButton.classList.add('remove');
  removeButton.textContent = '×';
  todoItem.appendChild(removeButton);

  todoList.appendChild(todoItem);
}

todoInput.addEventListener('keypress', function(event) {
  if (event.keyCode === 13) {
    const todoText = this.value.trim();
    if (todoText !== '') {
      addTodoToList(todoText);
      this.value = '';
    }
  }
});

// Check items when complete
todoList.addEventListener('change', function(event) {
  const target = event.target;
  if (target.classList.contains('check')) {
    target.parentElement.classList.toggle('selected', target.checked);
  }
});

// Delete items off the list
todoList.addEventListener('click', function(event) {
  const target = event.target;
  if (target.classList.contains('remove')) {
    target.parentElement.remove();
  }
});