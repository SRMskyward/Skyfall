void addRowsToTable(Table table, int numRows, float[] x1,
                                                        float[] AltitudeY,float[] OrientationxY,
                                                        float[] OrientationyY, float[] OrientationzY) 
  {
  for (int i = 0; i < x1.length; i++) {
    TableRow newRow = table.addRow();
    newRow.setFloat("Time", x1[i]);
    newRow.setFloat("Altitude", AltitudeY[i]);
    newRow.setFloat("OrientationX", OrientationxY[i]);
    newRow.setFloat("OrientationY", OrientationyY[i]);
    newRow.setFloat("OrientationZ", OrientationzY[i]);
  }
}
