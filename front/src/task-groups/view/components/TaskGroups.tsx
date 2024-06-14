import React from 'react';
import { Accordion, AccordionDetails, AccordionSummary, Typography, Grid, AccordionActions, Button } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { TaskGroup } from '../../types/TaskGroup';
import { useLocation } from 'wouter';
import { UseQueryResult } from '@tanstack/react-query';
import { TasksInGroup } from '../../types/TasksInGroup';

interface TaskGroupsProps {
  taskLists?: TaskGroup[];
  createTaskQuery: (taskGroupId: number) => UseQueryResult<TasksInGroup, Error>
}

interface TaskGroupViewProps {
  taskGroup: TaskGroup;
  createTaskQuery: (taskGroupId: number) => UseQueryResult<TasksInGroup, Error>
}

const TaskGroupView: React.FC<TaskGroupViewProps> = ({ taskGroup, createTaskQuery }) => {
  const [_, setLocation] = useLocation();
  const { isLoading, isError, data, error, refetch, isFetching } = createTaskQuery(taskGroup.id);

  const onChange = (_event: React.SyntheticEvent, newExpanded: boolean) => {
    if (newExpanded) {
      refetch();
    }
  }

  return (
    <Accordion onChange={onChange}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1a-content"
        id="panel1a-header"
      >
        <Typography variant='subtitle1'>{taskGroup.name}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        {data ? (
          data.tasks.length === 0 ? (
            <Typography variant='body1'>No tasks in this group</Typography>
          ) : (
            <Grid container spacing={1}>
              {data.tasks.map((task) => (
                <Grid item xs={12} key={task.id}>
                  <Typography variant='body1'>{task.name}</Typography>
                </Grid>
              ))}
            </Grid>
          )
        ) : isError ? (
          <span>Error: {error.message}</span>
        ) : isLoading ? (
          <span>Loading...</span>
        ) : (
          <span>Not ready ...</span>
        )}
        <div>{isFetching ? 'Fetching...' : null}</div>
      </AccordionDetails>
      <AccordionActions>
        <Button color='success' onClick={() => setLocation(`/${taskGroup.id}/tasks/new`)}>+ Task</Button>
      </AccordionActions>
    </Accordion>
  );
}

const TaskGroupsView: React.FC<TaskGroupsProps> = ({ taskLists, createTaskQuery }) => {

  return (
    <Grid container spacing={1}>
      {taskLists!.map((taskList: TaskGroup) => (
        <Grid item xs={12} key={taskList.id}>
          <TaskGroupView taskGroup={taskList} createTaskQuery={createTaskQuery} />
        </Grid>
      ))}
    </Grid>
  );
};

export default TaskGroupsView;