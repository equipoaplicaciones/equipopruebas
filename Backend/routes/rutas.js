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
        console.log("Datos recibidos:", req.body); // Verificar qué datos se reciben
        const fechaHora = new Date(`${fecha}T${hora}:00`);

        const nuevaCita = new Cita({ usuarioId, nombre, fecha: fechaHora, hora, descripcion });
        const citaGuardada = await nuevaCita.save();

        console.log("Cita guardada:", citaGuardada); // Verificar qué datos se guardan
        res.status(201).json({ mensaje: 'Cita agendada exitosamente', cita: citaGuardada });
    } catch (error) {
        console.error("Error al guardar la cita:", error);
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

  router.put('/:id/estado', async (req, res) => {
    const { id } = req.params;
    const { nuevoEstado } = req.body;
  
    try {
      const cita = await Cita.findById(id);
      if (!cita) {
        return res.status(404).json({ mensaje: 'Cita no encontrada' });
      }
  
      // Actualizar el estado
      cita.status = nuevoEstado;
      await cita.save();
  
      res.json({ mensaje: 'Estado actualizado correctamente', cita });
    } catch (error) {
      res.status(500).json({ mensaje: 'Error al actualizar el estado', error });
    }
  });

router.get('/api/usuario/:id/citas', async (req, res) => {
    try {
        const usuarioId = req.params.id;

        console.log("ID del usuario recibido:", usuarioId);

        // Asegúrate de convertir el usuarioId en ObjectId si es necesario
        const citas = await Cita.find({ usuarioId }).populate('usuarioId');

        console.log("Citas encontradas:", citas);

        res.status(200).json({ total: citas.length, citas });
    } catch (error) {
        console.error("Error al obtener las citas:", error);
        res.status(500).json({ mensaje: 'Error al obtener las citas del usuario', error: error.message });
    }
});

router.put('/api/citas/:id', async (req, res) => {
    try {
        const { fecha, hora } = req.body;
        const fechaHora = new Date(`${fecha}T${hora}:00`);

        // Buscar la cita y actualizar solo la fecha y la hora
        const citaActualizada = await Cita.findByIdAndUpdate(
            req.params.id,
            { fecha: fechaHora, hora },
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
const PDFDocument = require('pdfkit');
const fs = require('fs');
const path = require('path');
router.post('/api/citas/:usuarioId', async (req, res) => {
    const citaId = req.params.id; // ID de la cita
    const usuarioId = req.params.usuarioId; // El usuarioId ahora viene de la URL
    const { nombre, fecha, hora, descripcion } = req.body;

    try {
        console.log('Datos recibidos:', { citaId, nombre, fecha, hora, descripcion, usuarioId });

        // Validar los datos de entrada
        if (!nombre || !fecha || !hora || !descripcion || !usuarioId) {
            return res.status(400).json({ mensaje: 'Todos los campos son obligatorios.' });
        }

        const fechaHora = new Date(`${fecha}T${hora}:00`);
        if (isNaN(fechaHora.getTime())) {
            return res.status(400).json({ mensaje: 'Fecha u hora inválidas.' });
        }

        // Verificar si ya existe una cita en esa fecha y hora
        const citaExistente = await Cita.findOne({ fecha: fechaHora, usuarioId });
        if (citaExistente) {
            console.log('Cita ya existente');
            return res.status(400).json({ mensaje: 'Ya existe una cita en esa fecha y hora.' });
        }

        // Crear o actualizar la cita
        const nuevaCita = new Cita({
            _id: citaId,
            nombre,
            fecha: fechaHora,
            hora,
            descripcion,
            usuarioId, // Asociamos el usuarioId correctamente desde la URL
        });

        const citaGuardada = await nuevaCita.save();
        console.log('Cita guardada:', citaGuardada);

        // Crear PDF con los detalles de la cita
        const pdfDir = path.join(__dirname, 'citas');
        if (!fs.existsSync(pdfDir)) {
            fs.mkdirSync(pdfDir, { recursive: true });
        }

        const pdfPath = path.join(pdfDir, `cita_${citaGuardada._id}.pdf`);
        console.log('Ruta del PDF:', pdfPath);

        const doc = new PDFDocument({ margin: 50 });
        const stream = fs.createWriteStream(pdfPath);
        doc.pipe(stream);

        

        // Título con color y margen
        doc.fontSize(24).fillColor('#4CAF50').text('Comprobante de Cita', { align: 'center' });
        doc.moveDown();

        // Detalles de la cita con color y formato
        doc.fontSize(18).text('Detalles de la Cita', { underline: true, align: 'center' });
        doc.moveDown();

        doc.fontSize(14).text(`Nombre: ${nombre}`, { continued: true }).font('Helvetica-Bold');
        doc.text(` ${nombre}`, { font: 'Helvetica' });

        doc.fontSize(12).text(`Fecha: ${fecha}`);
        doc.text(`Hora: ${hora}`);
        doc.text(`Descripción: ${descripcion}`);
        
        doc.moveDown();
        doc.fillColor('#2196F3').text('Este documento es su pase de entrada a la cita.', { align: 'center', font: 'Helvetica-Bold' });

        // Borde alrededor del contenido
        doc.rect(40, 40, 520, 750).stroke();

        // Sección de firma
        doc.moveDown().text('Firma del paciente: ____________________', { align: 'left' });
        doc.text('Firma del consultorio: ____________________', { align: 'right' });

        doc.end();

        stream.on('finish', () => {
            console.log('PDF generado correctamente');
            res.status(200).json({
                mensaje: 'Cita creada exitosamente.',
                cita: citaGuardada,
            });
        });

        stream.on('error', (error) => {
            console.error('Error al generar el PDF:', error);
            res.status(500).json({
                mensaje: 'Cita creada, pero ocurrió un error al generar el PDF.',
                cita: citaGuardada,
                error: error.message,
            });
        });
    } catch (error) {
        console.error('Error al crear la cita:', error);
        res.status(500).json({ mensaje: 'Error al crear la cita.', error: error.message });
    }
});

router.get('/api/citas/:id/descargar', async (req, res) => {
    const citaId = req.params.id;

    try {
        // Verificar si la cita existe
        const cita = await Cita.findById(citaId);
        if (!cita) {
            return res.status(404).json({ mensaje: 'Cita no encontrada.' });
        }

        const pdfPath = path.join(__dirname, 'citas', `cita_${citaId}.pdf`);

        // Verificar si el archivo PDF existe
        if (!fs.existsSync(pdfPath)) {
            return res.status(404).json({ mensaje: 'PDF no encontrado.' });
        }

        // Enviar el archivo PDF
        res.download(pdfPath, `cita_${citaId}.pdf`, (err) => {
            if (err) {
                console.error('Error al enviar el archivo:', err);
                res.status(500).json({ mensaje: 'Error al descargar el PDF.', error: err.message });
            }
        });
    } catch (error) {
        console.error('Error en la descarga del PDF:', error);
        res.status(500).json({ mensaje: 'Error al procesar la solicitud.', error: error.message });
    }
});

module.exports = router;
