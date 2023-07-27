// Check items when complete

$(document).on("click", ".check", function() {

  $(this).toggleClass("selected");

});

// When enter key is pressed, append a new item to the list

$(".toDo").keydown(function(event) {
  if (event.which === 13) {
    var newItem = $("<li>" + $(this).val() + "<span class='check'></span><span class='remove'><img src='https://cdn4.iconfinder.com/data/icons/linecon/512/delete-512.png' alt='Remove' class='remove-icon'></span></li>");
    $(".list ul").append(newItem);
    $(this).val('');
  }
});

// Delete items off list

$(document).on("click", ".remove", function() {

  $(this).parent().remove();

});