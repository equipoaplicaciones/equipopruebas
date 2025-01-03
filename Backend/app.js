const express = require('express');
const mongoose = require('mongoose');
require('dotenv').config();
const rutas = require('./routes/rutas');
const cors = require('cors');


const usuarioRoutes = require('./models/usuario');

const app = express();
const PORT = 5000;

app.use(express.json());
app.use(cors());

mongoose.connect(process.env.MONGO_URI)
.then(() => console.log('conectado a MongoDB'))
.catch((error) => console.error('Error al conectar a MongoDB:', error));

app.get('/', (req, res) => {
    res.send('¡Servidor Conectado a MongoDB!');
});

app.use(rutas);

app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});