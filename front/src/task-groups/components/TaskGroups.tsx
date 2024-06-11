import React from 'react';
import TaskGroup from '../types/TaskGroup';
import { useQuery } from 'react-query';
import { getAllTaskGroups } from '../fetch';
import { Accordion, AccordionDetails, AccordionSummary, Typography, Grid } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

interface TaskGroupsProps {
}

const TaskGroups: React.FC<TaskGroupsProps> = () => {
  const { data: taskLists, isLoading, isError } = useQuery('taskGroups', getAllTaskGroups);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching task lists</div>;
  }

  return (
    <Grid container spacing={1}>
      {taskLists.map((taskList: TaskGroup) => (
        <Grid item xs={12} key={taskList.id}>
          <Accordion>
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography>{taskList.name}</Typography>
            </AccordionSummary>
            <AccordionDetails>
              Details
            </AccordionDetails>
          </Accordion>
        </Grid>
      ))}
    </Grid>
  );
};

export default TaskGroups;