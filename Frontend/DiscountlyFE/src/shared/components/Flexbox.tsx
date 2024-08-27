import React from "react";
import { Box, SxProps, Theme } from "@mui/material";

interface FlexboxProps {
  children: React.ReactNode;
  direction?: "row" | "column";
  justifyContent?:
    | "flex-start"
    | "center"
    | "flex-end"
    | "space-between"
    | "space-around"
    | "space-evenly";
  alignItems?: "flex-start" | "center" | "flex-end" | "stretch" | "baseline";
  flexWrap?: "nowrap" | "wrap" | "wrap-reverse";
  gap?: number | string;
  sx?: SxProps<Theme>; // MUI's system style prop for custom styling
}

export const Flexbox: React.FC<FlexboxProps> = ({
  children,
  direction = "row",
  justifyContent = "flex-start",
  alignItems = "stretch",
  flexWrap = "nowrap",
  gap = 0,
  sx = [],
}) => {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: direction,
        justifyContent,
        alignItems,
        flexWrap,
        gap,
        ...sx,
      }}
    >
      {children}
    </Box>
  );
};
