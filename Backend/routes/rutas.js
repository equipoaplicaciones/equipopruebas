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
    const { nombre, fecha, descripcion } = req.body;

    const nuevaCita = new Cita({
        nombre,
        fecha,  
        descripcion
    });

    try {
        const citaGuardada = await nuevaCita.save();
        res.status(201).json({
            mensaje: 'Cita agendada exitosamente',
            cita: citaGuardada
        });
    } catch (error) {
        // Si el error es de validación
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

module.exports = router;
