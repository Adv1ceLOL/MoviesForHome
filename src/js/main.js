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

const dropdowns = document.querySelectorAll(".dropdown");
dropdowns.forEach((dropdown) => {
 dropdown.addEventListener("click", (e) => {
  e.stopPropagation();
  dropdowns.forEach((c) => c.classList.remove("is-active"));
  dropdown.classList.add("is-active");
 });
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

// Group movies by category and render each section
function renderMovies(movieList) {
    const container = document.querySelector('.movie-list');
    // Group movies by their category
    const categories = {};
    movieList.forEach((movie) => {
        if (!categories[movie.category]) {
            categories[movie.category] = [];
        }
        categories[movie.category].push(movie);
    });
    
    // Iterate each category and render a section for it
    Object.keys(categories).forEach((cat) => {
        const section = `
        <div class="content-section">
            <div class="content-section-title">${cat}</div>
            <div class="apps-card">
                ${categories[cat].map(movie => `
                <div class="app-card">
                    <span>
                        <img src="${movie.poster}" alt="${movie.title}" class="movie-poster"">
                        ${movie.title}
                    </span>
                    <div class="app-card__subtext">${movie.description}</div>
                    <div class="app-card-buttons">
                        <div class="movie-rating">
                          <svg class="svg-stars" viewBox="0 0 24 24" stroke-linejoin="round">
                            <path d="M12 .587l3.668 7.431 8.2 1.193-5.934 5.787 1.402 8.179L12 18.897l-7.336 3.866 1.402-8.179L.132 9.211l8.2-1.193z"/>
                          </svg>
                          ${movie.rating} / 5
                        </div>
                        <button class="content-button status-button">Watch Trailer</button>
                        <div class="menu"></div>
                    </div>
                </div>
                `).join('')}
            </div>
        </div>
        `;
        container.innerHTML += section;
    });
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

// Filter movies by status when clicking on the header links: Popular, Latest, Upcoming
$('.main-header-link').on('click', function(e) {
  e.preventDefault();
  // Remove active class and set the clicked one as active
  $('.main-header-link').removeClass("is-active");
  $(this).addClass("is-active");
  
  // Get the status from the clicked link's text
  const status = $(this).text().trim();
  
  // Clear the movie list container before rendering the filtered movies
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  
  // Filter the movies array by the selected status
  const filteredMovies = movies.filter((movie) => movie.status === status);
  
  // Render the filtered movies
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

$('.header-profile').on('click', function(e) {
  // Remove preventDefault for navigation or manually set location:
  window.location.href = $(this).attr('href');
});

// Handler for "Comments"
$('.menu-link.notify').on('click', function(e) {
  e.preventDefault();
  $('.menu-link').removeClass('is-active');
  $(this).addClass('is-active');
  
  // Hide the movies container and show the comments container along with its wrappers
  $('.main-container').hide();
  $('.main-container-comments, .content-wrapper-comments, .comments-container').show();
  
  // Clear the comments container then render comments
  const container = document.querySelector('.comments-container');
  container.innerHTML = '';
  renderComments();
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

// Updated renderComments function using "comments-container"
function renderComments() {
  const container = document.querySelector('.comments-container');
  container.innerHTML = '';

  // Group the users by the movie they commented on
  const groupedComments = {};
  users.forEach((user) => {
    if (!groupedComments[user.commentedMovie]) {
      groupedComments[user.commentedMovie] = [];
    }
    groupedComments[user.commentedMovie].push(user);
  });

  // For each movie with comments, render one container that aggregates its comments
  Object.keys(groupedComments).forEach((movieTitle) => {
    const commentsHTML = groupedComments[movieTitle].map((user) => `
      <li class="adobe-product">
        <div class="products">
          <!-- Show the user profile image -->
          <img src="${user.profileImage}" alt="${user.name}" style="width: 28px; height: 28px; border-radius: 50%; margin-right: 16px;" />
          <!-- Show the user name -->
          <span>${user.name}</span>
        </div>
        <span class="status">
          <!-- Dynamically set the status-circle color based on the review -->
          <span class="status-circle" style="background-color: ${user.review === 'good' ? '#3bf083' : user.review === 'medium' ? '#ffeb3b' : '#ff5858'};"></span>
          ${user.comment}
        </span>
        <div class="button-wrapper">
          <div class="menu">
            <button class="dropdown">
              <ul>
                <li><a href="#">View All Comments</a></li>
                <li><a href="#">Edit Comments</a></li>
                <li><a href="#">Delete Comment</a></li>
              </ul>
            </button>
          </div>
        </div>
      </li>
    `).join("");
    
    container.innerHTML += `
      <div class="content-section">
        <div class="content-section-title">Comments for ${movieTitle}</div>
        <ul>
          ${commentsHTML}
        </ul>
      </div>
    `;
  });
}

$('.menu-link.notify').on('click', function(e) {
  e.preventDefault();
  // Remove active state from all links and add it to this one
  $('.menu-link').removeClass('is-active');
  $(this).addClass('is-active');
  
  // Clear the movie list container
  const container = document.querySelector('.movie-list');
  container.innerHTML = '';
  
  // Render the comments view
  renderComments();
});

// Array of users with comment details and review quality
const users = [
    {
        id: 1,
        name: 'John Doe',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'Heart of Stone (2023)',
        comment: 'Awesome movie! Loved it.',
        review: 'good'
    },
    {
        id: 2,
        name: 'Jane Smith',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Fall Guy (2024)',
        comment: 'Not my cup of tea.',
        review: 'bad'
    },
    {
        id: 3,
        name: 'Alice Johnson',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Killer (2023)',
        comment: 'Incredible storytelling!',
        review: 'good'
    },
    {
        id: 4,
        name: 'Michael Brown',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'Heart of Stone (2023)',
        comment: 'Stunning visuals!',
        review: 'good'
    },
    {
        id: 5,
        name: 'Emily Clark',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'Heart of Stone (2023)',
        comment: 'The story left to be desired',
        review: 'bad'
    },
    {
        id: 6,
        name: 'Robert Wilson',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Fall Guy (2024)',
        comment: 'Great storyline.',
        review: 'good'
    },
    {
        id: 7,
        name: 'Susan Davis',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Fall Guy (2024)',
        comment: 'Entertaining with a twist.',
        review: 'medium'
    },
    {
        id: 8,
        name: 'David Miller',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Killer (2023)',
        comment: 'An edge-of-your-seat thriller.',
        review: 'medium'
    },
    {
        id: 9,
        name: 'Laura Garcia',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Killer (2023)',
        comment: 'Intense and gripping.',
        review: 'good'
    },
    {
        id: 10,
        name: 'Kevin Anderson',
        profileImage: '../assets/images/logo.jpg',
        commentedMovie: 'The Killer (2023)',
        comment: 'Masterful direction.',
        review: 'good'
    }
];

// Sample movies list (9 movies, 3 per category) with status and rating properties
const movies = [
  // Action
  {
    title: 'Heart of Stone (2023)',
    category: 'Action',
    status: 'Popular',
    rating: 4.5,
    poster: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIPXZNJnBJl8qYCmMb4BmGGH5KplZzfYRv7o_Tn5HLLZw3kuMGmHyJqFy9u8xg3LPboXA&usqp=CAU',
    description: 'Gal Gadot stars as a global intelligence operative on a mission to protect a powerful AI system. The film received good reviews and was noted for its action sequences.'
  },
  {
    title: 'The Fall Guy (2024)',
    category: 'Action',
    status: 'Latest',
    rating: 4.0,
    poster: 'https://m.media-amazon.com/images/I/71ic9Y0ECYL.jpg',
    description: 'Starring Ryan Gosling and Emily Blunt, this action-comedy pays tribute to stunt performers. It has been praised for its humor and practical stunts.'
  },
  {
    title: 'The Beekeeper (2024)',
    category: 'Action',
    status: 'Latest',
    rating: 3.8,
    poster: 'https://lh3.googleusercontent.com/proxy/YFn3AAC_N_EcWBUODxGNyQpG6Kef8lqoonOLHsL7n7h-Bqsj8FWNEnGeDuBT5r38ITnMHNvVbIGpyfXRPXtQ4p_R5Bhjlg',
    description: 'Jason Statham plays a retired operative seeking revenge against online scammers. The film has been noted for its action sequences and Statham\'s performance.'
  },
  // Comedy
  {
    title: 'No Hard Feelings (2023)',
    category: 'Comedy',
    status: 'Popular',
    rating: 4.2,
    poster: 'https://lh4.googleusercontent.com/proxy/r_l7FGsaenInk9FKJRDCmHFenovmQ-thIzS8x5HFAWw3-ZgMq9n-Pq1UNhHVjk7KOjaTeccnePSOI7nvCwYWFMMaPw6x7gQYig61j_o',
    description: 'Jennifer Lawrence stars as a woman hired to date a wealthy couple\'s inexperienced son. The film garnered positive reviews for its humor and Lawrence\'s performance.'
  },
  {
    title: 'Four Mothers (2025)',
    category: 'Comedy',
    status: 'Upcoming',
    rating: "?",
    poster: 'https://m.media-amazon.com/images/M/MV5BZDIwY2U0ODMtNzIzNC00YmFlLTgxZjYtMjQ2Y2RhYTUxZDE0XkEyXkFqcGc@._V1_.jpg',
    description: 'This Irish comedy follows a novelist caring for his elderly mother and her friends.'
  },
  {
    title: 'One of Them Days (2025)',
    category: 'Comedy',
    status: 'Upcoming',
    rating: "?",
    poster: 'https://www.movieposters.com/cdn/shop/files/one-of-them-days_zddhjktv.jpg?v=1736361167',
    description: 'Featuring Keke Palmer and SZA, this comedy follows two roommates hustling to pay rent.'
  },
  // Thriller
  {
    title: 'The Killer (2023)',
    category: 'Thriller',
    status: 'Popular',
    rating: 4.7,
    poster: 'https://i.ebayimg.com/images/g/R4cAAOSwqHplQH2E/s-l400.jpg',
    description: 'Directed by David Fincher, this film stars Michael Fassbender as an assassin entangled in an international vendetta after a botched job. It received positive reviews for its direction and performances.'
  },
  {
    title: 'Rebel Ridge (2024)',
    category: 'Thriller',
    status: 'Latest',
    rating: 4.0,
    poster: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRGD77OenH_pBYUabyt3E1Qz6wZ2maxGNEcCQ&s',
    description: 'This thriller features Aaron Pierre as a military veteran confronting a corrupt small-town sheriff. The film has been praised for its intense action sequences and compelling narrative.'
  },
  {
    title: 'Trap (2024)',
    category: 'Thriller',
    status: 'Latest',
    rating: 3.2,
    poster: 'https://lh5.googleusercontent.com/proxy/GgmDH-AX4QdThj8dha7OEuxyhMF2kzLv7ha7Kexa584IgWizvhJGLluiRpvd-fIvBvA0WWCFU-2dKFsbMNeZjO9ymOB9yA',
    description: 'Starring Josh Hartnett, this film follows a father who discovers that a concert is a setup to capture him, revealing his secret identity as a notorious serial killer. The movie has received mixed reviews, with some critics appreciating its unexpected twists.'
  }
];

// Render all movies by category (existing function remains unchanged)
renderMovies(movies);