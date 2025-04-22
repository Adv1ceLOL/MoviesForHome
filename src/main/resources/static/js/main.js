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
  $('.add-comment-popup').css('display', 'flex').hide().fadeIn(150);});

// Hide popup
$(document).on('click', '.close-add-comment', function() {
  $('.add-comment-popup').fadeOut(150);
});

// Mostra popup nuovo film
$(document).on('click', '.add-movie-btn', function() {
  $('.add-movie-popup').css('display', 'flex').hide().fadeIn(150);
});

// Nascondi popup nuovo film
$(document).on('click', '.close-add-movie', function() {
  $('.add-movie-popup').fadeOut(150);
});

// Mostra popup nuovo film
$(document).on('click', '.add-user-btn', function() {
  $('.add-user-popup').css('display', 'flex').hide().fadeIn(150);
});

// Nascondi popup nuovo film
$(document).on('click', '.close-add-user', function() {
  $('.add-user-popup').fadeOut(150);
});