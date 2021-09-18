package com.company.books.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.books.backend.model.Libro;
import com.company.books.backend.model.dao.ILibroDao;
import com.company.books.backend.response.LibroResponseRest;

@Service
public class LibroServiceImpl implements ILibroService {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(LibroServiceImpl.class);

	@Autowired
	private ILibroDao libroDao;

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<LibroResponseRest> buscarLibros() {
		log.info("inicio metodo buscarLibros()");

		LibroResponseRest response = new LibroResponseRest();

		try {

			List<Libro> libro = (List<Libro>) libroDao.findAll();
			response.getLibroResponse().setLibro(libro);
			response.setMetadata("Respuesta ok", "200", "Respuesta exitosa");

		} catch (Exception e) {

			response.setMetadata("Respuessta NO OK", "500", "error al consultar el libro");
			log.error("error al consultar el libro ", e.getMessage());
			e.getStackTrace();
			return new ResponseEntity<LibroResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<LibroResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<LibroResponseRest> buscarPorId(Long id) {

		log.info("Inicio metodo buscarPorId(id)");

		LibroResponseRest response = new LibroResponseRest();
		List<Libro> list = new ArrayList<>();

		try {

			Optional<Libro> libro = libroDao.findById(id);

			if (libro.isPresent()) {
				list.add(libro.get());
				response.getLibroResponse().setLibro(list);

			} else {

				log.error("El libro no fue Encontrado");
				response.setMetadata("Respuesta NO OK", "404", "Libro no encontrado");
				return new ResponseEntity<LibroResponseRest>(response, HttpStatus.NOT_FOUND);

			}

		} catch (Exception e) {

			log.error("Error en la consulta");
			response.setMetadata("Respuesta NO OK", "500", "Error en la consulta");
			return new ResponseEntity<LibroResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setMetadata("Respuesta OK", "200", "respuesta exitosa");
		return new ResponseEntity<LibroResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<LibroResponseRest> crear(Libro libro) {
		log.info("Inicio metodo crear(libro)");
		LibroResponseRest response = new LibroResponseRest();
		List<Libro> list = new ArrayList<>();

		try {
			Libro libroGuardado = libroDao.save(libro);

			if (libroGuardado != null) {

				list.add(libroGuardado);
				response.getLibroResponse().setLibro(list);

			} else {

				log.error("El Libro no fue Guardado con exito");
				response.setMetadata("Respuesta NO OK", "400", "libro no guardado intenta mas tarde");
				return new ResponseEntity<LibroResponseRest>(response, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			log.error("Error en la creacion");
			response.setMetadata("Respuesta NO OK", "500", "Error al crear el libro");
			return new ResponseEntity<LibroResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setMetadata("Respuesta OK", "200", "Libro creado exitosamente");
		return new ResponseEntity<LibroResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<LibroResponseRest> actualizar(Libro libro, Long ind) {
		log.info("Inicio metodo actualizar");

		LibroResponseRest response = new LibroResponseRest();
		List<Libro> list = new ArrayList<>();

		try {

			Optional<Libro> libroBuscado = libroDao.findById(ind);

			if (libroBuscado.isPresent()) {
				
				libroBuscado.get().setNombre(libro.getNombre());
				libroBuscado.get().setDescripcion(libro.getDescripcion());
				libroBuscado.get().setCategoria(libro.getCategoria());

				Libro libroActualizar = libroDao.save(libroBuscado.get());

				if (libroActualizar != null) {
					
					response.setMetadata("respuest ok", "200", "Libro actualizado");
					list.add(libroActualizar);
					response.getLibroResponse().setLibro(list);
					
				} else {
					
					log.error("Error en actualizar el libro");
					response.setMetadata("respuest no ok", "500", "Libro NO actualizado");
					return new ResponseEntity<LibroResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {

				log.error("Error en actualizar Libro");
				response.setMetadata("respuest no ok", "404", "Libro NO encontrado para actualizar");
				return new ResponseEntity<LibroResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {

			log.error("Error en actualizar Libro", e.getMessage());
			response.setMetadata("respuest no ok", "500", "Libro NO actualizado");
			return new ResponseEntity<LibroResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<LibroResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<LibroResponseRest> eliminar(Long id) {
		log.info("Iniciio del metodo eliminar(id)");
		LibroResponseRest response = new LibroResponseRest();

		try {
			// eliminar el registro
			libroDao.deleteById(id);
			response.setMetadata("Respuesta ok", "200", "el Libro fue eliminado");

		} catch (Exception e) {

			log.error("Error al borrar el libro", e.getMessage());
			response.setMetadata("respuest no ok", "500", "Libro NO borrado");
			return new ResponseEntity<LibroResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<LibroResponseRest>(response, HttpStatus.OK);
	}

}
