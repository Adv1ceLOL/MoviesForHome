$(function () {
 $(".menu-link").click(function () {
  $(".menu-link").removeClass("is-active");
  $(this).addClass("is-active");
 });
});

$(function () {
 $(".main-header-link").click(function () {
  $(".main-header-link").removeClass("is-active");
  $(this).addClass("is-active");
 });
});

$(document).on('click', '.dropdown', function(e) {
  e.stopPropagation();
  // Close other open dropdowns
  $('.dropdown').not(this).removeClass('is-active');
  // Toggle this dropdown
  $(this).toggleClass('is-active');
});

// Close dropdowns when clicking outside
$(document).on('click', function() {
  $('.dropdown').removeClass('is-active');
});

$(document).on('click', '.flag-comment-link', function(e) {
  e.preventDefault();
  const $toast = $('.toast-flag');
  $toast.stop(true, true).fadeIn(200).delay(2000).fadeOut(400);
});

$(".search-bar input")
 .focus(function () {
  $(".header").addClass("wide");
 })
 .blur(function () {
  $(".header").removeClass("wide");
 });

const toggleButton = document.querySelector('.dark-light');

toggleButton.addEventListener('click', () => {
  document.body.classList.toggle('light-mode');
  toggleButton.classList.toggle('light-mode');
  const moon = document.getElementById('moon-icon');
  const sun = document.getElementById('sun-icon');

  if (moon.style.display === 'none') {
    // Sole tramonta, luna sorge
    sun.animate([
      { transform: 'translateY(0)', opacity: 1 },
      { transform: 'translateY(-40px)', opacity: 0 }
    ], { duration: 400, fill: 'forwards' });
    setTimeout(() => {
      sun.style.display = 'none';
      moon.style.display = 'block';
      moon.animate([
        { transform: 'translateY(40px)', opacity: 0 },
        { transform: 'translateY(0)', opacity: 1 }
      ], { duration: 400, fill: 'forwards' });
    }, 400);
  } else {
    // Luna tramonta, sole sorge
    moon.animate([
      { transform: 'translateY(0)', opacity: 1 },
      { transform: 'translateY(40px)', opacity: 0 }
    ], { duration: 400, fill: 'forwards' });
    setTimeout(() => {
      moon.style.display = 'none';
      sun.style.display = 'block';
      sun.animate([
        { transform: 'translateY(-40px)', opacity: 0 },
        { transform: 'translateY(0)', opacity: 1 }
      ], { duration: 400, fill: 'forwards' });
    }, 400);
  }
});

// Show popup
$(document).on('click', '.add-comment-btn', function() {
  fillMovieOptions();
  $('.add-comment-popup').css('display', 'flex').hide().fadeIn(150);});

// Hide popup
$(document).on('click', '.close-add-comment', function() {
  $('.add-comment-popup').fadeOut(150);
});