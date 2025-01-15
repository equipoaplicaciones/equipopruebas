const express = require('express');
const mongoose = require('mongoose');
require('dotenv').config();
const rutas = require('./routes/rutas');
const cors = require('cors');

const app = express();
const PORT = 5001;

app.use(express.json());
app.use(cors());

mongoose.connect('mongodb+srv://kev:qwerty123@kevin.joqkehr.mongodb.net/?retryWrites=true&w=majority&appName=Kevin', { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => console.log("Conectado a MongoDB Atlas"))
    .catch(err => console.log("Error al conectar a MongoDB Atlas: ", err));



app.get('/', (req, res) => {
    res.send('¡Servidor Conectado a MongoDB Atlas!');
});

app.use(rutas);

app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
