const express = require('express');
const router = express.Router();
const Usuario = require('../models/usuario');
const Cita = require('../models/cita');

// Ruta para crear un nuevo usuario
router.post('/api/usuario', async (req, res) => {
    try {
        const { nombre, email, contrasena } = req.body;
        const newUser = new Usuario({ nombre, email, contrasena });
        await newUser.save();
        res.status(201).json(newUser);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Ruta para obtener todos los usuarios
router.get('/api/usuarios', async (req, res) => {
    try {
        const usuarios = await Usuario.find();
        res.status(200).json(usuarios);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Ruta para obtener un usuario específico por ID
router.get('/api/usuario/:id', async (req, res) => {
    try {
        const usuario = await Usuario.findById(req.params.id);
        if (!usuario) {
            return res.status(404).json({ message: 'Usuario no encontrado' });
        }
        res.status(200).json(usuario);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Ruta para actualizar los datos de un usuario
router.put('/api/usuario/:id', async (req, res) => {
    try {
        const { nombre, email, contrasena } = req.body;
        const usuarioActualizado = await Usuario.findByIdAndUpdate(
            req.params.id,
            { nombre, email, contrasena },
            { new: true } 
        );
        if (!usuarioActualizado) {
            return res.status(404).json({ message: 'Usuario no encontrado' });
        }
        res.status(200).json(usuarioActualizado);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

router.post('/api/citas', async (req, res) => {
    const { nombre, fecha, hora, descripcion } = req.body;

    const fechaHora = new Date(`${fecha}T${hora}:00`);

    try {
        const citaExistente = await Cita.findOne({ fecha: fechaHora });
        
        if (citaExistente) {
            return res.status(400).json({
                mensaje: 'La fecha y hora seleccionadas ya están ocupadas',
            });
        }

        const nuevaCita = new Cita({
            nombre,
            fecha: fechaHora,  // Usar la fecha y hora combinada
            hora,  
            descripcion
        });

        const citaGuardada = await nuevaCita.save();

        res.status(201).json({
            mensaje: 'Cita agendada exitosamente',
            cita: citaGuardada
        });

    } catch (error) {
        if (error.name === 'ValidationError') {
            const errorMessages = [];
            for (const field in error.errors) {
                errorMessages.push(error.errors[field].message);
            }
            return res.status(400).json({ mensaje: 'Errores de validación', errores: errorMessages });
        }

        res.status(500).json({ mensaje: 'Error interno del servidor', error: error.message });
    }
});

router.get('/api/citas', async (req, res) => {
    try {
        const citas = await Cita.find(); // Obtiene todas las citas de la colección
        res.json({
            total: citas.length,
            citas,
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Error al obtener todas las citas" });
    }
});


router.get('/api/citas/:fecha', async (req, res) => {
    try {
      const { fecha } = req.params;
      const citas = await Cita.find({
        fecha: {
          $gte: new Date(fecha + "T00:00:00.000Z"), 
          $lt: new Date(fecha + "T23:59:59.999Z")  
        }
      });
      
      res.json(citas);
    } catch (error) {
      console.error(error);
      res.status(500).json({ error: "Error al obtener citas" });
    }
  });

module.exports = router;
