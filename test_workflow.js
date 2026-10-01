const http = require('http');

const request = (method, path, body = null, token = null) => {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'localhost',
      port: 8080,
      path: path,
      method: method,
      headers: {
        'Content-Type': 'application/json'
      }
    };
    if (token) options.headers['Authorization'] = `Bearer ${token}`;

    const req = http.request(options, res => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          resolve({ status: res.statusCode, data: data ? JSON.parse(data) : null });
        } catch (e) {
          resolve({ status: res.statusCode, data: data });
        }
      });
    });

    req.on('error', e => reject(e));
    if (body) req.write(JSON.stringify(body));
    req.end();
  });
};

const delay = ms => new Promise(res => setTimeout(res, ms));

async function run() {
  console.log("Starting script...");
  
  // 1. Create Admins
  let adminTokens = [];
  for(let i=1; i<=3; i++) {
    const email = `admin${i}_${Date.now()}@test.com`;
    const pwd = `Password123`;
    const res = await request('POST', '/api/auth/register-admin', {
      name: `Admin ${i}`,
      email: email,
      mobile: `999999999${i}`,
      password: pwd
    });
    if(res.status !== 200 && res.status !== 201) {
      console.log(`Failed to create admin ${i}`, res.data);
      // fallback to login if exists
      const loginRes = await request('POST', '/api/auth/login', { email, password: pwd });
      if(loginRes.data && loginRes.data.accessToken) adminTokens.push(loginRes.data.accessToken);
    } else {
      adminTokens.push(res.data.accessToken);
    }
  }
  
  if(adminTokens.length < 3) {
    console.log("Could not get 3 admin tokens");
    return;
  }
  console.log("Admins registered/logged in.");

  // 2. Create Farmer
  const farmerEmail = `farmer_${Date.now()}@test.com`;
  const farmerRes = await request('POST', '/api/farmer/register', {
    name: "Test Farmer",
    email: farmerEmail,
    mobile: `8888888888`,
    password: "Password123",
    state: "MH",
    village: "Pune",
    address: "Pune",
    postalCode: "411001"
  });
  
  if(!farmerRes.data || !farmerRes.data.accessToken) {
    console.log("Failed to create farmer", farmerRes.data);
    return;
  }
  const farmerToken = farmerRes.data.accessToken;
  console.log("Farmer registered.");

  // 3. Verify Unassigned for Admin 1
  const unassignedRes = await request('GET', '/admin/support', null, adminTokens[0]);
  console.log("Admin 1 Unassigned Support Requests count:", unassignedRes.data ? unassignedRes.data.length : 0);
  
  if(unassignedRes.data && unassignedRes.data.length > 0) {
    const supportReq = unassignedRes.data.find(r => r.userId === farmerRes.data.userId || r.userName === "Test Farmer");
    if(!supportReq) {
      console.log("Could not find the support request for the new farmer");
      return;
    }
    
    // 4. Admin 1 Assigns
    const assignRes = await request('POST', `/admin/support/${supportReq.supportRequestId}/assign`, null, adminTokens[0]);
    console.log("Admin 1 Assign status:", assignRes.status);

    // 5. Verify Removed from Unassigned for Admin 2
    const unassignedRes2 = await request('GET', '/admin/support', null, adminTokens[1]);
    const stillUnassigned = unassignedRes2.data && unassignedRes2.data.find(r => r.supportRequestId === supportReq.supportRequestId);
    console.log("Is still unassigned for Admin 2?", !!stillUnassigned);
    
    // 6. Admin 1 Transfers to Admin 2
    // We need Admin 2's ID
    const myProfile = await request('GET', '/api/auth/me', null, adminTokens[1]); // Wait, what is the profile endpoint? 
    // Let's just fetch all admins to get Admin 2 ID
    // Oh, /api/auth/me doesn't exist maybe. We can check /admin/support or somehow get Admin 2 ID.
    // Or we can just get it from the notification if possible. Or we can just get list of admins.
  }
}

run().catch(console.error);
