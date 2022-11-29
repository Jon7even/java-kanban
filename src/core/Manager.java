/*Привет, Дмитрий! Это был мега-странный глюк o_O. В чем магия и из-за чего это, я не понял. Как и то, почему именнов этом файле, хотя в других все ок.Однако, учту на будущее, что каждый залитый файл теперь надо проверять на гитхабе*/package core;import tasks.*;import java.util.ArrayList;import java.util.HashMap;import java.util.Objects;public class Manager {    private final HashMap<Integer, Task> tasks = new HashMap<>();    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();    private int idGenerate = 0; // Генератор ID    public int addNewTask(Task task) {        int id = ++idGenerate;        task.setId(id);        tasks.put(id, task);        return id;    }    public int addNewEpic(Epic epic) {        int id = ++idGenerate;        epic.setId(id);        epicTasks.put(id, epic);        return id;    }    public Integer addNewSubtask(Subtask subtask) {        Epic epic = getSelectEpic(subtask.getRelationEpicId());        if (epic == null) {            System.out.println("Такого Эпика нет, где-то подкралась ошибка.");            return -1;        } else {            int id = ++idGenerate;            subtask.setId(id);            subTasks.put(id, subtask);            epic.addSubtaskId(id);            return id;        }    }    public ArrayList<Task> getTasks() {        System.out.println("Текущий список всех простых задач:");        return new ArrayList<>(tasks.values());    }    public ArrayList<Epic> getEpics() {        System.out.println("Текущий список всех Эпиков:");        return new ArrayList<>(epicTasks.values());    }    public ArrayList<Subtask> getSubtask() {        System.out.println("Текущий список всех Подзадач:");        return new ArrayList<>(subTasks.values());    }    public ArrayList<Subtask> getAllSubTaskForEpic(int idEpic) {        Epic epic = getSelectEpic(idEpic);        if (epic == null) {            System.out.println("Такого Эпика нет, где-то подкралась ошибка.");            return null;        } else {            ArrayList<Subtask> subtask = new ArrayList<>();            ArrayList<Integer> search = epic.getRelationSubtaskId(); // получаем списки Подзадач у Эпиков            for (Integer i : search) {                subtask.add(getSelectSubtask(i)); // добавляем все найденные подзадачи            }            updateEpicStatus(epic);            return subtask;        }    }    public void updateTask(Task task) {        tasks.put(task.getId(), task);    }    public void updateEpic(Epic epic) {        epicTasks.put(epic.getId(), epic);        updateEpicStatus(epic);    }    public void updateSubtask(Subtask subtask) {        subTasks.put(subtask.getId(), subtask);        Epic epic = getSelectEpic(subtask.getRelationEpicId());        updateEpicStatus(epic);    }    public void deleteTasks() {        tasks.clear();    }    public void deleteSubtask() {        for (Epic epic : epicTasks.values()) {            if (!epic.getRelationSubtaskId().isEmpty()) {                epic.getRelationSubtaskId().clear(); // удаляем зависимости Эпиков            }        }        subTasks.clear(); // удаляем подзадачи    }    public void deleteEpics() {        deleteSubtask(); // сначала удаляем Подзадачи        epicTasks.clear(); // потом удаляем Эпики    }    public Task getSelectTask(int idSelect) {        if (tasks.get(idSelect) != null) {            return tasks.get(idSelect);        } else {            System.out.println("Задача с идентификатором " + idSelect + " не найдена!");        }        return null;    }    public Epic getSelectEpic(int idSelect) {        if (epicTasks.get(idSelect) != null) {            return epicTasks.get(idSelect);        } else {            System.out.println("Эпик с идентификатором " + idSelect + " не найден!");        }        return null;    }    public Subtask getSelectSubtask(int idSelect) {        if (subTasks.get(idSelect) != null) {            return subTasks.get(idSelect);        } else {            System.out.println("Подзадачи с идентификатором " + idSelect + " не найдено!");        }        return null;    }    public void removeSelectTask(int idSelect) {        if (tasks.get(idSelect) != null) {            tasks.remove(idSelect);            System.out.println("Задача с идентификатором " + idSelect + " удалена!");        } else {            System.out.println("Задача с идентификатором " + idSelect + " не найдена!");        }    }    public void removeSelectEpic(int idSelect) {        if (epicTasks.get(idSelect) != null) {            epicTasks.remove(idSelect);            System.out.println("Эпик с идентификатором " + idSelect + " удален!");        } else {            System.out.println("Эпик с идентификатором " + idSelect + " не найден!");        }    }    public void removeSelectSubtask(Integer idSelect) {        if (subTasks.get(idSelect) != null) {            Epic epic = getSelectEpic(subTasks.get(idSelect).getRelationEpicId()); // получаем нужный эпик            epic.getRelationSubtaskId().remove(idSelect); // удаляем зависимость            updateEpic(epic); // загружаем в базу            subTasks.remove(idSelect); // а потом можно удалить Подзадачу            System.out.println("Эпик с идентификатором " + idSelect + " удален!");        } else {            System.out.println("Эпик с идентификатором " + idSelect + " не найден!");        }    }    public void updateEpicStatus(Epic epic) {        boolean isAllSubtaskNew = false; // проверка если все Подзадачи имеют статус NEW        boolean isAllSubtaskDone = false; // проверка если все Подзадачи имеют статус DONE        if (!epic.getRelationSubtaskId().isEmpty()) {            ArrayList<Integer> search = epic.getRelationSubtaskId();            for (Integer i : search) {                Task task = getSelectSubtask(i);                isAllSubtaskNew = task.getStatus() != TaskStatus.DONE && task.getStatus() != TaskStatus.IN_PROGRESS;            }            for (Integer i : search) {                Task task = getSelectSubtask(i);                isAllSubtaskDone = task.getStatus() != TaskStatus.NEW && task.getStatus() != TaskStatus.IN_PROGRESS;            }        }        if (epic.getRelationSubtaskId().isEmpty() || isAllSubtaskNew) {            epic.autoSetStatus(TaskStatus.NEW);        } else if (isAllSubtaskDone) {            epic.autoSetStatus(TaskStatus.DONE);        } else {            epic.autoSetStatus(TaskStatus.IN_PROGRESS);        }    }    @Override    public boolean equals(Object o) {        if (this == o) return true;        if (o == null || getClass() != o.getClass()) return false;        Manager manager = (Manager) o;        return idGenerate == manager.idGenerate && tasks.equals(manager.tasks) && epicTasks.equals(manager.epicTasks)                && subTasks.equals(manager.subTasks);    }    @Override    public int hashCode() {        return Objects.hash(tasks, epicTasks, subTasks, idGenerate);    }    @Override    public String toString() {        return "Manager{" +                "tasks=" + tasks +                ", epicTasks=" + epicTasks +                ", subTasks=" + subTasks +                ", idGenerate=" + idGenerate +                '}' + "\n";    }}