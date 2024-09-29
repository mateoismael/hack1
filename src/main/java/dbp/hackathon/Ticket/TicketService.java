package dbp.hackathon.Ticket;

import dbp.hackathon.Estudiante.Estudiante;
import dbp.hackathon.Estudiante.EstudianteRepository;
import dbp.hackathon.Funcion.Funcion;
import dbp.hackathon.Funcion.FuncionRepository;
import dbp.hackathon.Ticket.Email.TicketEmailEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FuncionRepository funcionRepository;

    public Ticket createTicket(Long estudianteId, Long funcionId, Integer cantidad) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        Funcion funcion = funcionRepository.findById(funcionId).orElse(null);
        if (estudiante == null || funcion == null) {
            throw new IllegalStateException("Estudiante or Funcion not found!");
        }

        Ticket ticket = new Ticket();
        ticket.setEstudiante(estudiante);
        ticket.setFuncion(funcion);
        ticket.setCantidad(cantidad);
        ticket.setEstado(Estado.VENDIDO);
        ticket.setFechaCompra(LocalDateTime.now());

        // Generar código QR y asignarlo al ticket
        String qrCode = UUID.randomUUID().toString();
        ticket.setQr(qrCode);

        // Guardar ticket en la base de datos
        Ticket savedTicket = ticketRepository.save(ticket);

        // Generar URL del código QR
        String qrCodeUrl = generateQRCode(qrCode);

        // Preparar los datos para el correo
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("nombre", estudiante.getName());
        templateModel.put("nombrePelicula", funcion.getNombre());
        templateModel.put("fechaFuncion", funcion.getFecha().toString());
        templateModel.put("cantidadEntradas", cantidad.toString());
        templateModel.put("precioTotal", String.valueOf(cantidad * funcion.getPrecio()));
        templateModel.put("ticketId", savedTicket.getId().toString());
        templateModel.put("qrCodeUrl", qrCodeUrl);

        // Enviar el evento para el correo
        eventPublisher.publishEvent(new TicketEmailEvent(estudiante.getEmail(), "Tu entrada de cine", "email-template", templateModel));

        return savedTicket;
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    public Iterable<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Iterable<Ticket> findByEstudianteId(Long estudianteId) {
        return ticketRepository.findByEstudianteId(estudianteId);
    }

    public void changeState(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            throw new IllegalStateException("Ticket not found!");
        }
        ticket.setEstado(Estado.CANJEADO);
        ticketRepository.save(ticket);
    }
    public String generateQRCode(String data) {
        return "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + data;
    }

}
