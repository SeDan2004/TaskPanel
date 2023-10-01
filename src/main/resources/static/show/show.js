var wrapper = $(".wrapper")[0];
var updateDescriptionWin = $(".update_description_win")[0];
var updateDescriptionAccept = $(".update_description_accept")[0];
var updateDescriptionClose = $(".update_description_close")[0];
var updateIdTask;

var descriptionWin = $(".description_win")[0];
var descriptionAcceptBtn = $(".description_accept_btn")[0];
var tasksNotFound = $(".tasks_not_found_txt")[0];
var weekDayContent = $(".weekday_content")[0];
var paginationBtns = [...$(".pagination_btns a")];
var [weekDayInd, page, contentCount] = getPageInf();
var tasksCount;
var pagNumBtnsCount;

getCountPaginationBtns();
addPaginationAuto();
addEvent();

function createTask(idTask, description) {
  function getSvgByUrl(svgFileName) {
    let path = "/TaskPanel/icons/" + svgFileName,
	    svg;
				  
	$.get({
      method: "GET",
	  url: path,
	  async: false,
	  success(doc) {
	    let svgClass = "";
						  
		svg = doc.querySelector("svg");
						  
		if (svgFileName === "Карандаш.svg") {
		  svgClass = "update_task";
		}
						  
		if (svgFileName === "Отмена.svg") {
		  svgClass = "delete_task";
	    }
						  
	    svg.classList.add(svgClass);
	  }
	})
				  
    return svg;  
  }
  
  task = $(
    `
      <div class="task" id="${idTask}">
	      <p>${description}</p>
		  <div class="task_btns_box">
		      <div class="task_btns"></div>
		  </div>
      </div>  
    `
  )[0];
		
  taskBtns = task.children[1].children[0];
		      
  [updateBtn, deleteBtn] = ["Карандаш.svg", "Отмена.svg"].map(getSvgByUrl);
		
  updateBtn.addEventListener("click", changeWrapperVisibility);
  deleteBtn.addEventListener("click", delTask);
		
  taskBtns.appendChild(updateBtn);
  taskBtns.appendChild(deleteBtn);
  
  return task;
}

function addTask() {
  let tasks = getTasks();
  
  // if (!confirm("Вы уверены?")) return;
  
  if (descriptionWin.value.trim().length === 0) {
    alert("Вы ничего не ввели");
    return;
  }
     
  $.ajax({
    method: "POST",
	url: "../../add",
	async: false,
	data: {description: descriptionWin.value, weekDayInd: weekDayInd},
	success(arg) {
	  if (isFinite(arg)) {
	    idTask = +arg;
			  			  
	    if (tasksNotFound !== undefined) {
		  tasksNotFound.style.display = "none";		  
	    }
			  	
	    if (tasksCount >= contentCount) {
		  tasks[tasks.length - 1].remove();  			  
	    }
	    
	    task = createTask(idTask, descriptionWin.value);
	    
	    tasksCount++;
	    weekDayContent.prepend(task);
	  } else {
	    error = arg;
	    alert(error);
	    return;
	  }
		  
	  descriptionWin.value = "";
    }
  })
  
  if (tasksCount > contentCount) {
    if (tasksCount % contentCount === 1) {
	  pagNumBtnsCount = Math.ceil(tasksCount / contentCount);
	  
      if (getComputedStyle(paginationBtns[3]).display === "none") {
	    dynamicPagBtn();
	  }
    }
  }
}

function getTasks() {
  return weekDayContent.querySelectorAll(".task");
}

function changeWrapperVisibility() {	
  if (this.classList.contains("update_task")) {
	updateIdTask = this.parentNode.parentNode.parentNode.id;
	
    wrapper.style.display = "block";
    TweenMax.to(wrapper, 0.5, {opacity: 1});
  }
  
  if (this.classList.contains("update_description_close")) {
	updateIdTask = undefined;  
	
    TweenMax.to(wrapper, 0.5, {opacity: 0, onComplete: () => {
	  wrapper.style.display = "none";
	}})
  }
}

function updateTask() {
  if (updateDescriptionWin.value.trim().length === 0) {
    alert("Вы ничего не ввели");
	return;
  }
  
  $.ajax({
    method: "POST",
	url: "../../update",
	data: {idTask: updateIdTask, description: updateDescriptionWin.value},
	success() {
	  $(`#${updateIdTask} p`)[0].innerText = updateDescriptionWin.value;
	  updateDescriptionWin.value = "";
	  updateDescriptionClose.click();     
	}
  })
}

function delTask() {
  let currentTask = this.parentNode.parentNode.parentNode,
      idTask = currentTask.id;
  
  if (!confirm("Вы уверены?")) return;
  
  $.ajax({
    method: "POST",
    url: "../../delete",
    async: false,
    data: {
	  idTask: idTask,
	  weekDayInd: weekDayInd,
	  nextPageNum: pagNumBtnsCount > page ? page + 1 : -1,
	  contentCount: contentCount
	},
	success(arg) {	
	  if (arg !== "") {
	    if (arg.startsWith("[")) {
		  [idTask, description] = JSON.parse(arg);
		  
		  currentTask.remove();
		  task = createTask(idTask, description);
		  
		  $(".pagination_btns").before(task);
		  tasksCount--;
		} else {
		  error = arg;
		  alert(error);
		  return;
		}
	  } else {
	    currentTask.remove();
	    
	    if (tasksCount === 1) {
		  tasksNotFound.style.display = "block";
		}
	    
	    tasksCount--;
	  }
	}
  })
  
  if (tasksCount >= contentCount) {
    if (tasksCount % contentCount === 0) {
	  dynamicPagBtn();
	}
  }
}

function dynamicPagBtn() {
  let pagNumBtns = paginationBtns.slice(1, paginationBtns.length - 1),
      next = paginationBtns[paginationBtns.length - 1];
  
  function showPagBtn() {
    let notHiddenBtn = paginationBtns.slice(1)
                                     .find(pagBtn => getComputedStyle(pagBtn).display !== "none");
    
    if (notHiddenBtn === undefined) {
	  next.style.display = "block";
	  
	  for (i = 0; i < pagNumBtns.length - 1; i++) {
	    let pageNum = i + 1,
	        pagBtn = pagNumBtns[i],
	        updatedPage = "/" + pageNum + "/";
	    
	    if (i === 0) {
		  pagBtn.classList.add("active");
		}
		
		if (i === 1) {
		  next.setAttribute("href", getUpdatedLocationHref(updatedPage));
		}
		
		pagBtn.style.display = "block";
		pagBtn.innerText = pageNum;
	    pagBtn.setAttribute("href", getUpdatedLocationHref(updatedPage));
	  }
	} else {
	  updatedPage = "/" + 3 + "/";
		
	  pagNumBtns[2].style.display = "block";
	  pagNumBtns[2].innerText = 3;
	  pagNumBtns[2].setAttribute("href", getUpdatedLocationHref(updatedPage));
	}
  }
	
  function delPagBtn() {
	if (getComputedStyle(pagNumBtns[2]).display === "block") {
	  pagNumBtns[2].style.display = "none";
	  pagNumBtns[2].innerText = "";
	  pagNumBtns[2].removeAttribute("href");
	} else {
	  next.style.display = "none";
	  next.removeAttribute("href");
	  
	  for (i = 0; i < pagNumBtns.length - 1; i++) {
	    let pagBtn = pagNumBtns[i];
	    
	    pagBtn.innerText = "";
	    pagBtn.style.display = "none";
	    pagBtn.removeAttribute("href");
	  }
	}
  }
	
  if (dynamicPagBtn.caller === addTask) showPagBtn();
  if (dynamicPagBtn.caller === delTask) delPagBtn();
}

function getPageInf() {
  regexp = /\d{0,}\/\d{0,}\/\d{0,}$/;
  pageInf = regexp.exec(location.href)[0].split("/");
  return pageInf.map(num => +num);
}

function getCountPaginationBtns() {
  $.ajax({
    method: "POST",
	url: "../../get_count_tasks",
	async: false,
	data: {weekDayInd: weekDayInd},
	success(arg) {
	  tasksCount = +arg;
	  pagNumBtnsCount = tasksCount / contentCount;
			
	  if (pagNumBtnsCount % 1 !== 0) {
	    pagNumBtnsCount = Math.ceil(pagNumBtnsCount);
	  }
	}
  })
}

function getUpdatedLocationHref(updatedPage) {
  let currentPageStr = "/" + page + "/";
  return location.href.replace(currentPageStr, updatedPage);
}

function addPaginationAuto() {
  let back = paginationBtns[0],
	  next = paginationBtns.slice(-1)[0];
	  
  function setNumsOnBtn() {
    let pagNumBtns = paginationBtns.slice(1, paginationBtns.length - 1),
        pagBtnsLen = pagNumBtns.length,
        pagBtnsInd = page / pagBtnsLen;
        
    if (pagBtnsInd % 1 === 0) {
	  pagBtnsInd -= 1;
	} else {
	  pagBtnsInd = Math.floor(pagBtnsInd);
	}
	
	startBtnsInd = 1 + pagBtnsLen * pagBtnsInd;
	
	for (i = 0; i < pagBtnsLen; i++) {
	  pagBtn = pagNumBtns[i];
	  
	  if (startBtnsInd > pagNumBtnsCount) break;
	  	  
	  pagBtn.style.display = "block";
	  pagBtn.innerText = startBtnsInd;
	  
	  if (startBtnsInd === page) {
	    pagBtn.classList.add("active"); 
	  } else {
		updatedPage = "/" + startBtnsInd + "/";
	    pagBtn.setAttribute("href", getUpdatedLocationHref(updatedPage));
	  }
	  
	  startBtnsInd++;
	}	                                   
  }

  if (tasksCount <= contentCount) return;
    
  if (page > 1) {
    updatedPage = "/" + (page - 1) + "/";
    
    back.style.display = "block";
    back.setAttribute("href", getUpdatedLocationHref(updatedPage));
  }
  
  if (page !== pagNumBtnsCount) {
    updatedPage = "/" + (page + 1) + "/";
		
	next.style.display = "block";
	next.setAttribute("href", getUpdatedLocationHref(updatedPage));  
  }
  
  setNumsOnBtn();
}

function checkTextarea() {
  
}

function addEvent() {
  descriptionAcceptBtn?.addEventListener("click", addTask);
  
  tasks = getTasks();
  
  for (i = 0; i < tasks.length; i++) {
    updateBtn = tasks[i].querySelector(".update_task");
    deleteBtn = tasks[i].querySelector(".delete_task");
    
    updateBtn.addEventListener("click", changeWrapperVisibility);
    deleteBtn.addEventListener("click", delTask);
  }
  
  updateDescriptionAccept.addEventListener("click", updateTask);
  updateDescriptionClose.addEventListener("click", changeWrapperVisibility);
}