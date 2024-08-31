// COMPONENTS
import Pagination from "@mui/material/Pagination";
import Stack from "@mui/material/Stack";
import { Flexbox } from "../../shared/components/Flexbox";

const PaginationComponent: React.FC = () => {
  return (
    <Flexbox justifyContent="center" alignItems="center">
      <Stack spacing={2}>
        <Pagination count={10} shape="rounded" size="large" />
        <Pagination count={10} variant="outlined" shape="rounded" />
      </Stack>
    </Flexbox>
  );
};

export default PaginationComponent;
