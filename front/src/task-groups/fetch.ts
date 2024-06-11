import TaskGroup from "./types/TaskGroup";

// TODO use TaskList type also to return data
const baseUrl = 'http://localhost:8080';

export const getAllTaskGroups = async () => {
  const response = await fetch(`${baseUrl}/task-groups`);
  const data = await response.json();
  return data;
}

export const createTaskGroup = async (newTaskList: TaskGroup) : Promise<string> => {
  const response = await fetch(`${baseUrl}/task-groups`, {
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

export const getTaskGroup = async (id: string) => {
  const response = await fetch(`${baseUrl}/task-groups/${id}`);

  if (!response.ok) {
    throw new Error('Network response was not ok');
  }

  const data = await response.json();
  return data;
};