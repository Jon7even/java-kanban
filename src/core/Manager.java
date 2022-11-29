/*Привет, Дмитрий! Это был мега-странный глюк o_O. В чем магия и из-за чего это, я не понял. Как и то, почему именно
в этом файле, хотя в других все ок.
Однако, учту на будущее, что каждый залитый файл теперь надо проверять на гитхабе))*/

package core;
import tasks.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();

    private int idGenerate = 0; // Генератор ID
}