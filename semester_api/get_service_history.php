<?php
header('Content-Type: application/json'); include 'connect.php';
$customer_id = isset($_POST['customer_id']) ? intval($_POST['customer_id']) : 0;
if ($customer_id <= 0) { echo json_encode([]); exit; }
$query = "
  SELECT b.id,
         b.booking_date,
         b.booking_time,
         b.total,
         b.status,
         wd.workshop_name AS workshop_name,
         wd.address,
         COALESCE(b.technician, '') AS technician
  FROM bookings b
  LEFT JOIN workshop_details wd ON wd.provider_id = b.provider_id
  WHERE b.customer_id = ? AND b.status IN ('completed','closed')
  ORDER BY b.id DESC";
$stmt = $con->prepare($query);
$stmt->bind_param('i', $customer_id);
$stmt->execute();
$res = $stmt->get_result();
$data = [];
while ($row = $res->fetch_assoc()) {
    $services = [];
    $stmt2 = $con->prepare("SELECT s.name FROM booking_items bi JOIN provider_services s ON s.id = bi.service_id WHERE bi.booking_id = ?");
    $stmt2->bind_param('i', $row['id']);
    $stmt2->execute();
    $r2 = $stmt2->get_result();
    while ($s = $r2->fetch_assoc()) { $services[] = $s['name']; }
    $stmt2->close();
    $data[] = [
        'id' => (int)$row['id'],
        'workshop_name' => $row['workshop_name'] ?: 'Workshop',
        'status' => $row['status'] ?: 'completed',
        'booking_date' => $row['booking_date'] ?: '',
        'booking_time' => $row['booking_time'] ?: '',
        'address' => $row['address'] ?: '',
        'services' => $services,
        'total' => (float)$row['total'],
        'technician' => $row['technician']
    ];
}
echo json_encode($data);
$stmt->close(); $con->close();
?>