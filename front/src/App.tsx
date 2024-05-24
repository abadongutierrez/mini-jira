import { useQuery } from 'react-query';
import { Route, Switch } from "wouter";
import './App.css'
import TaskLists from './task-lists/components/TaskLists'
import { getAllTaskLists } from './task-lists/fetch';
import NewTaskList from './task-lists/components/NewTaskList';
import TaskListDetail from './task-lists/components/TaskListDetail';

function App() {
  const { data: taskLists, isLoading, isError } = useQuery('taskLists', getAllTaskLists);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching task lists</div>;
  }

  return (
    <div>
      <Switch>
        <Route path="/task-list/:id">
          {(params) => params.id === 'new' ? <NewTaskList /> : <TaskListDetail id={params.id} />}
        </Route>
        <Route path="/task-list">
          <TaskLists taskLists={taskLists} />
        </Route>
        <Route path="/">
          Home
        </Route>
        {/* Add more routes as needed */}
      </Switch>
    </div>
  )
}

export default App