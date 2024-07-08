package fr.diginamic.hello.controleurs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.repository.VilleRepository;
import fr.diginamic.hello.service.VilleService;
import jakarta.validation.Valid;

/**Définit les routes liées aux villes
 * 
 */
@RestController
@RequestMapping("/villes")
public class VilleControleur {
	
	@Autowired
	private VilleService service;
	@Autowired
	private VilleRepository repository;

	/**Ressort toutes les villes
	 * 
	 */
	@GetMapping
	public List<Ville> trouverVilles() {
		return service.extractVilles();
	}

	/**Ressort une ville
	 * @param id l'ID de la ville à trouver
	 */
	@GetMapping("/{id}")
	public Ville trouverVille(@PathVariable int id) {
		return service.extractVille(id);
	}
	
	/**Ressort une liste de villes commençant par une chaine de caractères donnés
	 * @param string la chaine de caractères à trouver
	 */
	@GetMapping("/nom/{string}")
	public List<Ville> trouverVillesCommencantPar(@PathVariable String string) {
		return repository.getByNomIsStartingWith(string);
	}
	
	@GetMapping("/minHab/{min}")
	public List<Ville> trouverVillesParHabitantsMin(@PathVariable int min) {
		return repository.getByNbHabitantsGreaterThan(min);
	}
	
	@GetMapping("/minHab/{min}/{max}")
	public List<Ville> trouverVillesParHabitantsMinEtMax(@PathVariable int min, @PathVariable int max) {
		return repository.getByNbHabitantsBetween(min, max);
	}
	
	@GetMapping("parDep/{id}/minHab/{min}")
	public List<Ville> trouverVillesDUnDepParHabitantsMin(@PathVariable String id, @PathVariable int min){
		return repository.getByDepartementIdAndNbHabitantsGreaterThan(id, min);
	}
	
	@GetMapping("parDep/{id}/minMaxHab/{min}/{max}")
	public List<Ville> trouverVillesDUnDepParHabitantsMinMax(@PathVariable String id, @PathVariable int min, @PathVariable int max){
		return repository.getByDepartementIdAndNbHabitantsBetween(id, min, max);
	}
	
	@GetMapping("parDep/{id}/TopNb/{nb}")
	public List<Ville> trouverTopNVillesDUnDep(@PathVariable String id, @PathVariable int nb){
		Pageable pageable = PageRequest.of(0, nb);
		return repository.findByDepartementIdOrderByNbHabitantsDesc(id, pageable);
	}

	/**Insere une ville dans la base de donnée
	 * @param ville La ville à inserer
	 */
	@PostMapping
	public ResponseEntity<String> ajouterVille( @Valid @RequestBody Ville nvVille, BindingResult errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getAllErrors().stream().map(e->e.getDefaultMessage()).collect(Collectors.joining("\n")));
		}
		service.insertVille(nvVille);
		return ResponseEntity.ok("Ville ajoutée");
	}
	
	/**Modifie une ville donnée
	 * @param id L'id de la ville à modifier
	 * @param ville Les nouvelles données
	 */
	@PutMapping("/{id}")
	public ResponseEntity<String> modifierVille(@PathVariable int id, @Valid @RequestBody Ville ville, BindingResult errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getAllErrors().get(0).getDefaultMessage());
		}
		service.updateVille(id, ville);
		return ResponseEntity.ok("Ville modifiée");
	}

	/**Supprime une ville donnée
	 * @param id L'id de la ville à supprimer
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> supprimerVille(@PathVariable int id) {
		Ville ville = trouverVille(id);
		if (ville != null) {
			service.deleteVille(id);
			return ResponseEntity.ok("Ville supprimée");

		}
		return ResponseEntity.badRequest().body("Cette ville n'existe pas");
	}

}
