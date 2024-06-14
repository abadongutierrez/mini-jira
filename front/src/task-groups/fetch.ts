import { Task } from "./types/Task";
import { TaskGroup } from "./types/TaskGroup";
import { TasksInGroup } from "./types/TasksInGroup";

// TODO use TaskList type also to return data
const baseUrl = 'http://localhost:8080';

export const getAllTaskGroups = async (): Promise<TaskGroup[]> => {
  const response = await fetch(`${baseUrl}/task-groups`);
  const data = await response.json();
  return data as TaskGroup[];
}

export const createTaskGroup = async (newTaskList: TaskGroup) => {
  const response = await fetch(`${baseUrl}/task-groups`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(newTaskList),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }

  console.log(response.headers);
  // const location = response.headers.get('Location')!;
  // const data = await response.json();
  // return data;
};

export const getTaskGroup = async (id: string) => {
  const response = await fetch(`${baseUrl}/task-groups/${id}`);

  if (!response.ok) {
    throw new Error('Network response was not ok');
  }

  const data = await response.json();
  return data;
};

export const createTaskInTaskGroup = async ({ taskGroupId, task } : { taskGroupId: string, task: Task }) => {
  const response = await fetch(`${baseUrl}/task-groups/${taskGroupId}/tasks`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(task),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
}

export const getTasksInTaskGroup = async (taskGroupId: number): Promise<TasksInGroup> => {
  const response = await fetch(`${baseUrl}/task-groups/${taskGroupId}/tasks`);
  const data = await response.json();
  return data as TasksInGroup;
}