import { useQuery } from 'react-query';
import { getTaskList } from '../fetch';

interface TaskListDetailProps {
  id: string;
}

const TaskListDetail: React.FC<TaskListDetailProps> = ({ id }) => {
  const { data: taskList, isLoading, isError } = useQuery(['taskList', id], () => getTaskList(id));

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError || !taskList) {
    return <div>Error fetching task list</div>;
  }

  return (
    <div>
      <h1>{taskList.name}</h1>
      <p>Type: {taskList.type}</p>
      <p>Status: {taskList.status}</p>
    </div>
  );
}

export default TaskListDetail;