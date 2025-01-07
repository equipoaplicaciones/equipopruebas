const express = require('express');
const router = express.Router();
const Usuario = require('../models/usuario'); // Modelo de usuario
const Cita = require('../models/cita'); // Ajusta la ruta si es necesario


// Ruta para registrar usuarios
router.post('/api/registro', async (req, res) => {
    const { nombre, email, contrasena } = req.body;

    console.log('Datos recibidos:', req.body); // Verifica que los datos estén llegando

    try {
        // Verifica si ya existe el usuario
        const usuarioExistente = await Usuario.findOne({ email });
        if (usuarioExistente) {
            return res.status(400).json({ mensaje: 'El correo ya está registrado' });
        }

        const nuevoUsuario = new Usuario({ nombre, email, contrasena });
        console.log('Usuario a guardar:', nuevoUsuario); // Verifica el objeto antes de guardarlo

        await nuevoUsuario.save(); // Intenta guardarlo
        res.status(201).json({ mensaje: 'Usuario registrado con éxito', usuario: nuevoUsuario });
    } catch (error) {
        console.error('Error al registrar usuario:', error); // Captura cualquier error
        res.status(500).json({ mensaje: 'Error al registrar usuario', error: error.message });
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
    const { usuarioId, nombre, fecha, hora, descripcion } = req.body;

    try {
        const fechaHora = new Date(`${fecha}T${hora}:00`);

        const citaExistente = await Cita.findOne({ usuarioId, fecha: fechaHora });
        if (citaExistente) {
            return res.status(400).json({
                mensaje: 'El usuario ya tiene una cita en la fecha y hora seleccionadas',
            });
        }

        const nuevaCita = new Cita({
            usuarioId,
            nombre,
            fecha: fechaHora,
            hora,
            descripcion
        });

        const citaGuardada = await nuevaCita.save();

        res.status(201).json({
            mensaje: 'Cita agendada exitosamente',
            cita: citaGuardada
        });
    } catch (error) {
        res.status(500).json({ mensaje: 'Error al agendar la cita', error: error.message });
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

  // Ruta para obtener las citas de un usuario por ID (MongoDB ID)
  router.get('/api/usuario/:id/citas', async (req, res) => {
    try {
        const usuarioId = req.params.id;

        // Verificar que el ID se recibe correctamente
        console.log("ID del usuario: ", usuarioId);

        const citas = await Cita.find({ usuarioId });

        res.status(200).json({
            total: citas.length,
            citas
        });
    } catch (error) {
        console.error("Error al obtener las citas:", error); // Verificar el error en el backend
        res.status(500).json({ mensaje: 'Error al obtener las citas del usuario', error: error.message });
    }
});



router.put('/api/citas/:id', async (req, res) => {
    try {
        const { nombre, fecha, hora, descripcion } = req.body;
        const fechaHora = new Date(`${fecha}T${hora}:00`);

        const citaActualizada = await Cita.findByIdAndUpdate(
            req.params.id,
            { nombre, fecha: fechaHora, hora, descripcion },
            { new: true }
        );

        if (!citaActualizada) {
            return res.status(404).json({ mensaje: 'Cita no encontrada' });
        }

        res.status(200).json({
            mensaje: 'Cita actualizada exitosamente',
            cita: citaActualizada
        });
    } catch (error) {
        res.status(500).json({ mensaje: 'Error al actualizar la cita', error: error.message });
    }
});

router.delete('/api/citas/:id', async (req, res) => {
    try {
        const citaEliminada = await Cita.findByIdAndDelete(req.params.id);

        if (!citaEliminada) {
            return res.status(404).json({ mensaje: 'Cita no encontrada' });
        }

        res.status(200).json({
            mensaje: 'Cita eliminada exitosamente'
        });
    } catch (error) {
        res.status(500).json({ mensaje: 'Error al eliminar la cita', error: error.message });
    }
});
// Ruta para obtener el ID de MongoDB de un usuario por su correo electrónico
router.get('/api/usuario/mongodb/:email', async (req, res) => {
    try {
        const email = req.params.email;  // Obtiene el correo electrónico de la URL
        const usuario = await Usuario.findOne({ email });

        if (!usuario) {
            return res.status(404).json({ mensaje: 'Usuario no encontrado' });
        }

        // Responder con el ID de MongoDB del usuario
        res.status(200).json({ mongodbUserId: usuario._id });
    } catch (error) {
        res.status(500).json({ mensaje: 'Error al obtener el ID de MongoDB', error: error.message });
    }
});

router.post('/api/citas/:id', async (req, res) => {
    const usuarioId = req.params.id; // ID del usuario desde la URL
    const { nombre, fecha, hora, descripcion } = req.body;

    try {
        const fechaHora = new Date(`${fecha}T${hora}:00`);

        // Validar si el usuario existe (opcional)
        const usuario = await Usuario.findById(usuarioId);
        if (!usuario) {
            return res.status(404).json({ mensaje: 'Usuario no encontrado' });
        }

        // Validar si ya existe una cita en esa fecha y hora para el usuario
        const citaExistente = await Cita.findOne({ usuarioId, fecha: fechaHora });
        if (citaExistente) {
            return res.status(400).json({
                mensaje: 'El usuario ya tiene una cita en la fecha y hora seleccionadas',
            });
        }

        // Crear una nueva cita
        const nuevaCita = new Cita({
            usuarioId,
            nombre,
            fecha: fechaHora,
            hora,
            descripcion,
        });

        const citaGuardada = await nuevaCita.save();

        res.status(201).json({
            mensaje: 'Cita creada exitosamente',
            cita: citaGuardada,
        });
    } catch (error) {
        res.status(500).json({ mensaje: 'Error al crear la cita', error: error.message });
    }
});






module.exports = router;
