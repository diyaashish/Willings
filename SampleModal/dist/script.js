$(function() {

  // Vars
  var modBtn  = $('#modBtn'),
      modal   = $('#modal'),
      close   = modal.find('.close'),
      modContent = modal.find('.modal-content');

  // open modal when click on open modal button 
  modBtn.on('click', function() {
    modal.css('display', 'block');
    modContent.removeClass('modal-animated-out').addClass('modal-animated-in');
  });

  // close modal when click on close button or somewhere out the modal content 
  $(document).on('click', function(e) {
    var target = $(e.target);
    if(target.is(modal) || target.is(close)) {
      modContent.removeClass('modal-animated-in').addClass('modal-animated-out').delay(300).queue(function(next) {
        modal.css('display', 'none');
        next();
      });
    }
  });

});