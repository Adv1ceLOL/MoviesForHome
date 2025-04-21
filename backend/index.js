require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');

const app = express();
app.use(cors());
app.use(express.json());

const pool = new Pool({
    user: process.env.DB_USER,
    host: process.env.DB_HOST,
    database: process.env.DB_DATABASE,
    password: process.env.DB_PASSWORD,
    port: process.env.DB_PORT,
});

// Get all movies
app.get('/api/movies', async (req, res) => {
  const result = await pool.query('SELECT * FROM movies ORDER BY id');
  res.json(result.rows);
});

app.get('/api/movies/:id/reviews', async (req, res) => {
  const movieId = req.params.id;
  const result = await pool.query(
    `SELECT reviews.*, users.username, users.profile_image_path
     FROM reviews 
     JOIN users ON reviews.user_id = users.id 
     WHERE movie_id = $1 ORDER BY reviews.id`, [movieId]);
  res.json(result.rows);
});

// Get all users (optional)
app.get('/api/users', async (req, res) => {
  const result = await pool.query('SELECT id, username, email, is_admin, created_at, profile_image_path FROM users ORDER BY id');
  res.json(result.rows);
});

// Add a review (example POST endpoint)
app.post('/api/movies/:id/reviews', async (req, res) => {
  const movieId = req.params.id;
  const { user_id, rating, content } = req.body;
  const result = await pool.query(
    `INSERT INTO reviews (rating, content, user_id, movie_id) VALUES ($1, $2, $3, $4) RETURNING *`,
    [rating, content, user_id, movieId]
  );
  res.json(result.rows[0]);
});

const PORT = 3001;
app.listen(PORT, () => {
  console.log(`API server running on port ${PORT}`);
});