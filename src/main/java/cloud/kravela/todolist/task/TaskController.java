package cloud.kravela.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.kravela.todolist.utils.Utils;
import jakarta.persistence.Entity;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	@Autowired
	private ITaskRepository taskRepository;
	
	@PostMapping("/")
	public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
		var userId = request.getAttribute("userId");
		taskModel.setUserId((UUID) userId);
		
		var currentDate = LocalDateTime.now();
		if(currentDate.isAfter(taskModel.getStartAt())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Data início maior que atual");
		}
		
		if(taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Data início maior que fim");
		}
		
		
		var task = this.taskRepository.save(taskModel);
		return ResponseEntity.status(HttpStatus.OK).body(task);
	}
	
	@GetMapping("/")
	public List<TaskModel> list(HttpServletRequest request) {
		var userId = request.getAttribute("userId");
		var tasks = this.taskRepository.findByUserId((UUID) userId);
		return tasks;
	}
	
	@PutMapping("/{id}")
	public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
		//var userId = request.getAttribute("userId");
		
		var taskId = this.taskRepository.findById(id).orElse(null);
		
		Utils.copyNonNullProperties(taskModel, taskId);
		
		//taskModel.setUserId((UUID) userId);
		//taskModel.setId(id);
		return this.taskRepository.save(taskId);
	}
	
}
