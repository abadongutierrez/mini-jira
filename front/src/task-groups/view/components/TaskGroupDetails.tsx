
import { useQuery } from '@tanstack/react-query';
import { getTaskGroup } from '../../fetch';

interface TaskGroupDetailsProps {
  id: string;
}

const TaskGroupDetails: React.FC<TaskGroupDetailsProps> = ({ id }) => {
  const { data: taskList, isLoading, isError } = useQuery({ queryKey: ['taskList', id], queryFn: () => getTaskGroup(id) });

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

export default TaskGroupDetails;