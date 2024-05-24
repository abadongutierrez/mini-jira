
import React from 'react';
import TaskList from '../types/TaskList';

interface TaskListProps {
  taskLists: TaskList[];
}

const TaskLists: React.FC<TaskListProps> = ({ taskLists }) => {
  return (
    <div>
      {taskLists.map((taskList) => (
        <div key={taskList.id}>
          <h2>{taskList.name}</h2>
          <p>Type: {taskList.type}</p>
          <p>Status: {taskList.status}</p>
        </div>
      ))}
    </div>
  );
};

export default TaskLists;