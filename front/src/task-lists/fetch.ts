import TaskList from "./types/TaskList";

// TODO use TaskList type also to return data

export const getAllTaskLists = async () => {
  const response = await fetch('http://localhost:8080/task-list');
  const data = await response.json();
  return data;
}

export const createTaskList = async (newTaskList: TaskList) : Promise<string> => {
  const response = await fetch('http://localhost:8080/task-list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(newTaskList),
  });

  if (!response.ok) {
    throw new Error('Network response was not ok');
  }

  console.log(response.headers);
  const location = response.headers.get('Location')!;
  return location;
};

export const getTaskList = async (id: string) => {
  const response = await fetch(`http://localhost:8080/task-list/${id}`);

  if (!response.ok) {
    throw new Error('Network response was not ok');
  }

  const data = await response.json();
  return data;
};