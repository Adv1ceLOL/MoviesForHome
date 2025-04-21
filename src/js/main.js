let movies = []; // Will be filled from backend
let movieRatings = {}; // { movieId: averageRating }

function renderCategories(movieList) {
  const sideMenu = document.querySelector('.side-menu');
  if (!sideMenu) return;
  // Extract unique genres
  const genres = [...new Set(movieList.map(m => m.genre))];
  sideMenu.innerHTML = genres.map(genre => {
    // Insert <br> after 12 characters if the genre name is longer
    const displayGenre = genre.length > 12
      ? genre.slice(0, 12) + '<br>' + genre.slice(12)
      : genre;
    return `
      <a href="#" class="category-link" data-genre="${genre}">
        ${genreIcons[genre] || genreIcons["default"]}
        ${displayGenre}
      </a>
    `;
  }).join('');
}

fetch('http://192.168.1.11:3001/api/movies')
  .then(res => res.json())
  .then(async data => {
    movies = data;
    renderCategories(movies); 
    // Fetch ratings for all movies in parallel
    await Promise.all(movies.map(async (movie) => {
      const res = await fetch(`http://192.168.1.11:3001/api/movies/${movie.id}/reviews`);
      const reviews = await res.json();
      if (reviews.length > 0) {
        const avg = reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length;
        movieRatings[movie.id] = avg.toFixed(1);
      } else {
        movieRatings[movie.id] = 'N/A';
      }
    }));
    renderMovies(movies);
  })
  .catch(err => {
    console.error('Error fetching movies:', err);
    alert('Error fetching movies: ' + err);
  });

$(document).on('click', '.category-link', function(e) {
  e.preventDefault();
  const genre = $(this).data('genre');
  const filteredMovies = movies.filter(m => m.genre === genre);
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  renderMovies(filteredMovies);
});

// Group movies by genre and render each section
function renderMovies(movieList) {
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  const genres = {};
  movieList.forEach((movie) => {
    if (!genres[movie.genre]) {
      genres[movie.genre] = [];
    }
    genres[movie.genre].push(movie);
  });

  Object.keys(genres).forEach((genre) => {
    const section = `
      <div class="content-section">
        <div class="content-section-title">${genre}</div>
        <div class="apps-card">
          ${genres[genre].map(movie => `<div class="app-card">
            <span>
              <img src="${movie.cover_image_path}" alt="${movie.title}" class="movie-poster">
              ${movie.title} (${movie.year})
            </span>
            <div class="app-card__subtext">${movie.description}</div>
            <div class="app-card-buttons">
              <div class="movie-rating">
                <svg class="svg-stars" viewBox="0 0 24 24" stroke-linejoin="round">
                  <path d="M12 .587l3.668 7.431 8.2 1.193-5.934 5.787 1.402 8.179L12 18.897l-7.336 3.866 1.402-8.179L.132 9.211l8.2-1.193z"/>
                </svg>
                ${movieRatings[movie.id] !== undefined ? movieRatings[movie.id] : 'N/A'} / 5
              </div>
              <button class="view-comments-btn" data-movie-id="${movie.id}" data-movie-title="${movie.title}">View Comments</button>
            </div>
          </div>`).join('')}
        </div>
      </div>
    `;
    container.innerHTML += section;
  });
}

function renderComments(movieId, movieTitle) {
  const container = document.querySelector('.comments-container');
  container.innerHTML = '<div class="content-section-title">Loading comments...</div>';
  fetch(`http://192.168.1.11:3001/api/movies/${movieId}/reviews`)
    .then(res => res.json())
    .then(reviews => {
      console.log(reviews);
      let commentsHTML = '';
      if (reviews.length === 0) {
        commentsHTML = '<div class="content-section-title">No comments for this movie yet.</div>';
      } else {
        commentsHTML = `
          <div class="content-section">
            <div class="content-section-title">Comments for ${movieTitle}</div>
            <ul>
              ${reviews.map(review => `
                <li class="adobe-product">
                  <div class="products">
                    <img src="${review.profile_image_path || '/images/profiles/default.jpg'}" alt="${review.username}" style="width: 28px; height: 28px; border-radius: 50%; margin-right: 16px;" />
                    <span>${review.username || 'User'}</span>
                  </div>
                  <span class="status">
                    <span class="status-circle" style="background-color: ${review.rating >= 4 ? '#3bf083' : review.rating === 3 ? '#ffeb3b' : '#ff5858'};"></span>
                    ${review.content.length > 60
                      ? review.content.slice(0, 60) + '<br>' + review.content.slice(60)
                      : review.content}
                  </span>
                  <div class="button-wrapper">
                    <div class="menu">
                      <button class="dropdown">
                        <ul>
                          <li><a href="#" class="flag-comment-link">Flag Comment</a></li>
                          <li><a href="#">Delete Comment</a></li>
                        </ul>
                      </button>
                    </div>
                  </div>
                </li>
              `).join('')}
            </ul>
          </div>
        `;
      }
      container.innerHTML = commentsHTML;
    })
    .catch(err => {
      container.innerHTML = '<div class="content-section-title">Error loading comments.</div>';
      console.error('Error fetching reviews:', err);
    });
}

// Handler for "View Comments" button click (already correct)
$(document).on('click', '.view-comments-btn', function() {
  const movieId = $(this).data('movie-id');
  const movieTitle = $(this).data('movie-title');
  $('.main-container').hide();
  $('.main-container-comments, .content-wrapper-comments, .comments-container').show();
  $('.menu-link').removeClass('is-active');
  $('.menu-link.notify').addClass('is-active');
  renderComments(movieId, movieTitle);
});


// Handler for "Comments" tab (show all comments for all movies)
$('.menu-link.notify').on('click', function(e) {
  e.preventDefault();
  $('.menu-link').removeClass('is-active');
  $(this).addClass('is-active');
  $('.main-container').hide();
  $('.main-container-comments, .content-wrapper-comments, .comments-container').show();

  // Fetch and render comments for all movies
  const container = document.querySelector('.comments-container');
  container.innerHTML = '<div class="content-section-title">Loading all comments...</div>';

  // Fetch all movies, then fetch reviews for each
  fetch('http://192.168.1.11:3001/api/movies')
    .then(res => res.json())
    .then(async moviesData => {
      let allCommentsHTML = '';
      for (const movie of moviesData) {
        // Fetch reviews for this movie
        const res = await fetch(`http://192.168.1.11:3001/api/movies/${movie.id}/reviews`);
        const reviews = await res.json();
        if (reviews.length > 0) {
          allCommentsHTML += `
            <div class="content-section">
              <div class="content-section-title">Comments for ${movie.title}</div>
              <ul>
                ${reviews.map(review => {
                  const content = review.content.length > 60
                    ? review.content.slice(0, 60) + '<br>' + review.content.slice(60)
                    : review.content;
                  return `
                    <li class="adobe-product">
                      <div class="products">
                        <img src="${review.profile_image_path && review.profile_image_path !== '/images/profiles/default.jpg' ? review.profile_image_path : '/images/profiles/default.jpg'}" alt="${review.username}" style="width: 28px; height: 28px; border-radius: 50%; margin-right: 16px;" />                        
                        <span>${review.username || 'User'}</span>
                      </div>
                      <span class="status">
                        <span class="status-circle" style="background-color: ${review.rating >= 4 ? '#3bf083' : review.rating === 3 ? '#ffeb3b' : '#ff5858'};"></span>
                        ${content}
                      </span>
                      <div class="button-wrapper">
                        <div class="menu">
                          <button class="dropdown">
                            <ul>
                              <li><a href="#" class="flag-comment-link">Flag Comment</a></li>
                              <li><a href="#">Delete Comment</a></li>
                            </ul>
                          </button>
                        </div>
                      </div>
                    </li>
                  `;
                }).join('')}
              </ul>
            </div>
          `;
        }
      }
      container.innerHTML = allCommentsHTML || '<div class="content-section-title">No comments yet.</div>';
    })
    .catch(err => {
      container.innerHTML = '<div class="content-section-title">Error loading comments.</div>';
      console.error('Error fetching all reviews:', err);
    });
});

// Handler for "Movies" tab
$('.menu-link').not('.notify').on('click', function(e) {
  e.preventDefault();
  $('.menu-link').removeClass('is-active');
  $(this).addClass('is-active');
  $('.main-container-comments, .content-wrapper-comments').hide();
  $('.main-container').show();
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  renderMovies(movies);
});

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
});

// Add your movie-specific JavaScript functionality here
function addMovieReview(movie) {
    const reviewsContainer = document.querySelector('.apps-card');
    const reviewCard = `
        <div class="app-card">
            <span>
                <img src="${movie.poster}" alt="${movie.title}" style="width: 50px; height: 70px; object-fit: cover;">
                ${movie.title}
            </span>
            <div class="app-card__subtext">${movie.description}</div>
            <div class="app-card-buttons">
                <button class="content-button status-button">Read Review</button>
                <div class="menu"></div>
            </div>
        </div>
    `;
    reviewsContainer.innerHTML += reviewCard;
}

$('.menu-link-main').on('click', function(e) {
  e.preventDefault();
  // Remove "is-active" from both all-movies and header links
  $('.menu-link-main').removeClass('is-active');
  $('.main-header-link').removeClass('is-active');
  
  // Set the clicked "All Movies" as active
  $(this).addClass('is-active');
  
  // Clear the movie list container
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  
  // Render all movies
  renderMovies(movies);
});

$('.side-menu a').on('click', function(e) {
  e.preventDefault();
  // Get the text of the clicked link (e.g., "Action", "Comedy", "Drama")
  const category = $(this).text().trim();
  
  // Clear the movie list container before rendering the filtered movies
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  
  // Filter the movies array by the selected category
  const filteredMovies = movies.filter((movie) => movie.category === category);
  
  // Render the filtered movies
  renderMovies(filteredMovies);
});

$('.main-header-link').on('click', function(e) {
  e.preventDefault();
  $('.main-header-link').removeClass("is-active");
  $(this).addClass("is-active");

  const status = $(this).text().trim();
  let filteredMovies = [];

  if (status === "Popular") {
    filteredMovies = movies.filter(movie => parseFloat(movieRatings[movie.id]) >= 4.9);
  } else if (status === "Latest") {
    filteredMovies = movies.filter(movie => parseInt(movie.year) >= 2010);
  } else if (status === "Upcoming") {
    const currentYear = new Date().getFullYear();
    filteredMovies = movies.filter(movie => parseInt(movie.year) > currentYear);
  } else {
    filteredMovies = movies;
  }

  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  renderMovies(filteredMovies);
});

$('.menu-link-main').on('click', function(e) {
  e.preventDefault();
  // Remove "is-active" class from all menu-link-main elements if desired
  $('.menu-link-main').removeClass('is-active');
  $(this).addClass('is-active');
  
  // Clear the movie list container
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  
  // Render all movies
  renderMovies(movies);
});

// Listen for input events on the search-bar input field
$('.search-bar input').on('input', function () {
  // Get the current input value and convert to lowercase for case-insensitive comparison
  const query = $(this).val().trim().toLowerCase();
  
  // Filter the movies array by matching the query with the movie title
  const filteredMovies = movies.filter(movie =>
    movie.title.toLowerCase().includes(query)
  );
  
  // Clear the movie list container
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  
  // Render the filtered movies
  renderMovies(filteredMovies);
});

$('.profile-img').on('click', function(e) {
  window.location.href = 'login.html';
});

// Handler for "Movies" (links that are not .notify)
$('.menu-link').not('.notify').on('click', function(e) {
  e.preventDefault();
  $('.menu-link').removeClass('is-active');
  $(this).addClass('is-active');
  
  // Hide the comments container and its wrapper, show the movies container again
  $('.main-container-comments, .content-wrapper-comments').hide();
  $('.main-container').show();
  
  // Clear and render movies
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  renderMovies(movies);
});

function fillMovieOptions() {
  const select = document.querySelector('.add-comment-form select[name="movie"]');
  if (!select) return;
  select.innerHTML = movies.map(m => `<option value="${m.title}">${m.title}</option>`).join('');
}

// Show popup
$(document).on('click', '.add-comment-btn', function() {
  fillMovieOptions();
  $('.add-comment-popup').css('display', 'flex').hide().fadeIn(150);});

// Hide popup
$(document).on('click', '.close-add-comment', function() {
  $('.add-comment-popup').fadeOut(150);
});

// Submit new comment
$(document).on('submit', '.add-comment-form', function(e) {
  e.preventDefault();
  const comment = this.comment.value.trim();
  const commentedMovie = this.movie.value;
  const review = this.review.value;
  if (!comment || !commentedMovie || !review) return;

  users.push({
    id: users.length + 1,
    name: 'Advice',
    profileImage: '../assets/images/logo.jpg',
    commentedMovie,
    comment,
    review
  });

  $('.add-comment-popup').fadeOut(150);
  renderComments();
});

const genreIcons = {
  "Drammatico": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z"/></svg>`,
  "Commedia": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M8 15s1.5 2 4 2 4-2 4-2"/><path d="M9 9h.01"/><path d="M15 9h.01"/></svg>`,
  "Thriller": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 2 7 13 12 24 7 13 2"/><polyline points="2 17 13 22 24 17"/></svg>`,
  "Azione": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 3v4"/><path d="M8 3v4"/></svg>`,
  "Fantascienza": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><ellipse cx="12" cy="12" rx="10" ry="6"/><path d="M2 12a10 6 0 0020 0"/></svg>`,
  "Animazione": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="10" rx="2"/><circle cx="12" cy="8" r="4"/></svg>`,
  "Avventura": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2l4 8H8l4-8zm0 20v-6m0 0l-4-8m4 8l4-8"/></svg>`,
  "Storico": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 9h16"/></svg>`,
  "Western": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M8 16l4-8 4 8"/></svg>`,
  "Musical": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="6" cy="18" r="3"/><circle cx="18" cy="18" r="3"/><path d="M9 18V5l12-2v13"/></svg>`,
  "Horror": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="8" cy="10" r="1"/><circle cx="16" cy="10" r="1"/><path d="M8 16c1.333-1 2.667-1 4 0"/></svg>`,
  "Commedia/Drammatico": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="9" cy="10" r="1"/><circle cx="15" cy="10" r="1"/><path d="M8 16c1.5-2 6.5-2 8 0" /></svg>`,  
  "Fantasy": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2l2 7h7l-5.5 4 2 7-5.5-4-5.5 4 2-7L3 9h7z"/></svg>`,
  "Guerra": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="10" rx="2"/><path d="M2 17l10-7 10 7"/><circle cx="12" cy="12" r="2"/></svg>`,
  "default": `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/></svg>`
};

// Render all movies by category (existing function remains unchanged)
renderMovies(movies);