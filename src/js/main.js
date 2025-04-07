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

// Sample movies list (9 movies, 3 per category)
const movies = [
  // Action
  {
    title: 'Heart of Stone (2023)',
    category: 'Action',
    poster: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIPXZNJnBJl8qYCmMb4BmGGH5KplZzfYRv7o_Tn5HLLZw3kuMGmHyJqFy9u8xg3LPboXA&usqp=CAU',
    description: 'Gal Gadot stars as a global intelligence operative on a mission to protect a powerful AI system. The film received mixed reviews but was noted for its action sequences.'
  },
  {
    title: 'The Fall Guy (2024)',
    category: 'Action',
    poster: 'https://m.media-amazon.com/images/I/71ic9Y0ECYL.jpg',
    description: 'Starring Ryan Gosling and Emily Blunt, this action-comedy pays tribute to stunt performers. It has been praised for its humor and practical stunts.'
  },
  {
    title: 'The Beekeeper (2024)',
    category: 'Action',
    poster: 'https://lh3.googleusercontent.com/proxy/YFn3AAC_N_EcWBUODxGNyQpG6Kef8lqoonOLHsL7n7h-Bqsj8FWNEnGeDuBT5r38ITnMHNvVbIGpyfXRPXtQ4p_R5Bhjlg',
    description: 'Jason Statham plays a retired operative seeking revenge against online scammers. The film has been noted for its action sequences and Statham\'s performance.'
  },
  // Comedy
  {
    title: 'No Hard Feelings (2023)',
    category: 'Comedy',
    poster: 'https://lh4.googleusercontent.com/proxy/r_l7FGsaenInk9FKJRDCmHFenovmQ-thIzS8x5HFAWw3-ZgMq9n-Pq1UNhHVjk7KOjaTeccnePSOI7nvCwYWFMMaPw6x7gQYig61j_o',
    description: 'Jennifer Lawrence stars as a woman hired to date a wealthy couple\'s inexperienced son. The film garnered positive reviews for its humor and Lawrence\'s performance.'
  },
  {
    title: 'Four Mothers (2025)',
    category: 'Comedy',
    poster: 'https://m.media-amazon.com/images/M/MV5BZDIwY2U0ODMtNzIzNC00YmFlLTgxZjYtMjQ2Y2RhYTUxZDE0XkEyXkFqcGc@._V1_.jpg',
    description: 'This Irish comedy follows a novelist caring for his elderly mother and her friends. It has been well-received for its witty dialogue and engaging storyline.'
  },
  {
    title: 'One of Them Days (2025)',
    category: 'Comedy',
    poster: 'https://www.movieposters.com/cdn/shop/files/one-of-them-days_zddhjktv.jpg?v=1736361167',
    description: 'Featuring Keke Palmer and SZA, this comedy follows two roommates hustling to pay rent. The film has received mixed reviews, with some praising its humor and performances.'
  },
  // Thriller
  {
    title: 'The Killer (2023)',
    category: 'Thriller',
    poster: 'https://i.ebayimg.com/images/g/R4cAAOSwqHplQH2E/s-l400.jpg',
    description: 'Directed by David Fincher, this film stars Michael Fassbender as an assassin entangled in an international vendetta after a botched job. It received positive reviews for its direction and performances.'
  },
  {
    title: 'Rebel Ridge (2024)',
    category: 'Thriller',
    poster: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRGD77OenH_pBYUabyt3E1Qz6wZ2maxGNEcCQ&s',
    description: 'This thriller features Aaron Pierre as a military veteran confronting a corrupt small-town sheriff. The film has been praised for its intense action sequences and compelling narrative.'
  },
  {
    title: 'Trap (2024)',
    category: 'Thriller',
    poster: 'https://lh5.googleusercontent.com/proxy/GgmDH-AX4QdThj8dha7OEuxyhMF2kzLv7ha7Kexa584IgWizvhJGLluiRpvd-fIvBvA0WWCFU-2dKFsbMNeZjO9ymOB9yA',
    description: 'Starring Josh Hartnett, this film follows a father who discovers that a concert is a setup to capture him, revealing his secret identity as a notorious serial killer. The movie has received mixed reviews, with some critics appreciating its unexpected twists.'
  }
];

// Render all movies by category
renderMovies(movies);