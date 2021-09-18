package com.company.books.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.books.backend.model.Categoria;
import com.company.books.backend.model.dao.ICategoriaDao;
import com.company.books.backend.response.CategoriaResponseRest;

@Service
public class CategoriaSereviceImpl implements ICategoriaService {

	private static final Logger log = LoggerFactory.getLogger(CategoriaSereviceImpl.class);

	@Autowired
	private ICategoriaDao categoriaDao;

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoriaResponseRest> buscarCategorias() {
		log.info("inicio metodo buscarCategorias()");

		CategoriaResponseRest response = new CategoriaResponseRest();

		try {
			List<Categoria> categoria = (List<Categoria>) categoriaDao.findAll();
			response.getCategoriaResponse().setCategoria(categoria);
			response.setMetadata("Respuesta ok", "200", "Respuesta Exitosa");

		} catch (Exception e) {
			response.setMetadata("Respuesta NO OK", "404", "Error al consultar categorias");
			log.error("error al consultar las categorias ", e.getMessage());
			e.getStackTrace();
			return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.NOT_FOUND); // devuelve un codigo 404
		}

		return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.OK); // devuelve un codigo 200
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoriaResponseRest> buscarPorId(Long id) {
		log.info("Inicio metodo buscarPorId(id)");

		CategoriaResponseRest response = new CategoriaResponseRest();
		List<Categoria> list = new ArrayList<>();

		try {
			Optional<Categoria> categoria = categoriaDao.findById(id);

			if (categoria.isPresent()) {
				list.add(categoria.get());
				response.getCategoriaResponse().setCategoria(list);

			} else {
				log.error("la categoria no fue encontrada");
				response.setMetadata("Respuesta NO OK", "404", "categoria no encontrada");
				return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			log.error("Error en la consulta");
			response.setMetadata("Respuesta NO OK", "500", "Error en la consulta");
			return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		response.setMetadata("Respuesta OK", "200", "respuesta exitosa");
		return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<CategoriaResponseRest> crear(Categoria categoria) {

		log.info("Inicio metodo crear(categoria)");

		CategoriaResponseRest response = new CategoriaResponseRest();
		List<Categoria> list = new ArrayList<>();

		try {

			Categoria categoriaGuardada = categoriaDao.save(categoria);

			if (categoriaGuardada != null) {
				list.add(categoriaGuardada);
				response.getCategoriaResponse().setCategoria(list);
			} else {

				log.error("la categoria no fue guardad con exito");
				response.setMetadata("Respuesta NO OK", "400", "categoria no guardada intenta mas tarde");
				return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {

			log.error("Error en la creacion");
			response.setMetadata("Respuesta NO OK", "500", "Error al crear la categoria");
			return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		response.setMetadata("Respuesta OK", "200", "Categoria creada exitosamente");
		return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<CategoriaResponseRest> actualizar(Categoria categoria, Long ind) {
		log.info("Inicio metodo actualizar");

		CategoriaResponseRest response = new CategoriaResponseRest();
		List<Categoria> list = new ArrayList<>();

		try {

			Optional<Categoria> CategoriaBuscada = categoriaDao.findById(ind);

			if (CategoriaBuscada.isPresent()) {
				CategoriaBuscada.get().setNombre(categoria.getNombre());
				CategoriaBuscada.get().setDescripcion(categoria.getDescripcion());

				Categoria cateoriaActualizar = categoriaDao.save(CategoriaBuscada.get());

				if (cateoriaActualizar != null) {
					response.setMetadata("respuest ok", "200", "Categoria actualizada");
					list.add(cateoriaActualizar);
					response.getCategoriaResponse().setCategoria(list);
				} else {
					log.error("Error en actualizar categoria");
					response.setMetadata("respuest no ok", "500", "Categoria NO actualizada");
					return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {

				log.error("Error en actualizar categoria");
				response.setMetadata("respuest no ok", "404", "Categoria NO encontrada para actualizar");
				return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {

			log.error("Error en actualizar categoria", e.getMessage());
			response.setMetadata("respuest no ok", "500", "Categoria NO actualizada");
			return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<CategoriaResponseRest> eliminar(Long id) {
		log.info("Iniciio del metodo eliminar(id)");
		CategoriaResponseRest response = new CategoriaResponseRest();

		try {
			// eliminar el registro
			categoriaDao.deleteById(id);
			response.setMetadata("Respuesta ok", "200", "La categoria fue eleminada");

		} catch (Exception e) {

			log.error("Error al borrar la categoria", e.getMessage());
			response.setMetadata("respuest no ok", "500", "Categoria NO borrada");
			return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<CategoriaResponseRest>(response, HttpStatus.OK);
	}

}
